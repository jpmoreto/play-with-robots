include <OpenSCAD-Arduino-Mounting-Library-master/arduino.scad>
include <nutsnbolts-master/cyl_head_bolt.scad>
include <nutsnbolts-master/materials.scad>

// falta:
//  - controlador dos motores
//  - multiplexer io ports
//  - acelarometro linear + bússola + acelarometro angular
//  - placa bluetooth

wheelDiameter = 6.5;
wheelWith     = 2.6;

carWidth     = 14;
carHeight    = 6.0;
carLength    = 2.8;
carThickness = 0.4;

motorLength = 3.7;
motorWith   = 2.5;
motorHeight = 2.5;
axisLength  = 0.5 + 1.778;
axisRadius  = 0.3/2.0;

wheelDistance       = carLength + 2 * 3.14 * carWidth/2  / 12 / 2 - 1.2;
wheelSideDistance   = carWidth/2 + 1.7;
wheelHeightDistance = -motorHeight/2;

speedSensorWheelDiameter     = 2.2;
speedSensorWheelWith         = 0.1;
speedSensorWheelSideDistance = carWidth/2 + 0.3;

transparencyBase = 1;
transparencyTop = 1;

//arduinoUno();
translate([1.1,0,0]) arduinoMega();

translate([1.0,0,0]) bateryHolder();

wheels();
wheelsSpeedSensor();
motors();

ultrasonicSensors();

mobilePhone();
mobilePhoneHolder();

base();
//top();

//specs();

// modules --------------------------------------------------------------------

module specs() {
  translate([15.0,12.0,0.0]) {
    text(text="Car external/internal dimensions:",size=1);  
    translate([1,-2.0,0]) 
      text(text=str("     length(x):  ",carWidth + carLength * 2,"  /   ",
        carLength * 2," + 2*",carWidth/2-carThickness," = ",carLength * 2 + carWidth-carThickness*2),size=1);  
    translate([1,-4.0,0]) 
      text(text=str("      width(y):  ",carWidth,"     /   ", carWidth-carThickness*2),size=1);  
    translate([1,-6.0,0]) 
      text(text=str("     height(z):  ",carHeight,"       /   ", carHeight-carThickness),size=1); 
    translate([1,-8.0,0]) text(text=str("thickness(z):  ",carThickness),size=1);  

    translate([0,-12.0,0]) text(text="Wheel Dimensions:",size=1); 
    translate([1,-14.0,0]) text(text=str("           width(y):  ",wheelWith),size=1);  
    translate([1,-16.0,0]) text(text=str("     diameter(z):  ",wheelDiameter),size=1);  
      
    translate([0,-20.0,0]) text(text=str("Motor height(z): ",motorHeight),size=1); 

    translate([0,-24.0,0]) text(text=str("Total height(z): ",motorHeight/2 + wheelDiameter/2 + carHeight),size=1); 
  }
}

module base() {
  difference() {
    // base
    hollowRoundedCube([carLength,carWidth,carHeight],/*12*/192,2*carThickness,transparencyBase, 360/(192*2));
    // laterals grooves
    translate([-2.0,0,4.5])
      cube([1,carWidth+2,0.2],true);
    translate([2.0,0,4.5]) rotate([0,180,0]) mirror([0,1,0])
      cube([1,carWidth+2,0.2],true);
  }
}

module arduinoUno() {
  translate([-6,-3.3,carThickness]) scale([0.1,0.1,0.1]) arduino(UNO);
}

module arduinoMega() {
  translate([-6,-5.0,carThickness]) scale([0.1,0.1,0.1]) arduino(MEGA);
}

module bateryHolder() {
  translate([4.3,-2.8,carThickness]) rotate([0,0,90]) bateryHolder2Sides6xAA();
}

module wheels() {
  /*color("DimGray")*/ {
    translate([wheelDistance, wheelSideDistance,wheelHeightDistance])  
      rotate([-90, 90, 0]) robotWheel(wheelDiameter,wheelWith);
    translate([wheelDistance,-wheelSideDistance,wheelHeightDistance])  
      rotate([90, 90, 0]) robotWheel(wheelDiameter,wheelWith);
    translate([-wheelDistance, wheelSideDistance,wheelHeightDistance]) 
      rotate([-90, 90, 0]) robotWheel(wheelDiameter,wheelWith);
    translate([-wheelDistance,-wheelSideDistance,wheelHeightDistance]) 
      rotate([90, 90, 0]) robotWheel(wheelDiameter,wheelWith);
  }
}

module wheelsSpeedSensor() {
  color("DarkGoldenrod") {
    translate([wheelDistance, speedSensorWheelSideDistance,wheelHeightDistance])  
      wheel(speedSensorWheelDiameter,speedSensorWheelWith);
    translate([wheelDistance,-speedSensorWheelSideDistance,wheelHeightDistance])  
      wheel(speedSensorWheelDiameter,speedSensorWheelWith);
    translate([-wheelDistance, speedSensorWheelSideDistance,wheelHeightDistance]) 
      wheel(speedSensorWheelDiameter,speedSensorWheelWith);
    translate([-wheelDistance,-speedSensorWheelSideDistance,wheelHeightDistance]) 
      wheel(speedSensorWheelDiameter,speedSensorWheelWith);
  }
}

module ultrasonicSensors() {
  // us front
  usFront();

  // us back
  rotate([0,0,180]) usFront();

  // us left side
  translate([-1, carWidth/2 - 0.8,  3.5]) rotate([90,0,90]) us();

  // us right side
  mirror([0,1,0])
    translate([-1, carWidth/2 - 0.8,  3.5]) rotate([90,0,90]) us();
}

module motors() {
  motorsFront();
  rotate([0,0,180]) motorsFront();

  frontSpeedSensor();
  rotate([0,0,180]) frontSpeedSensor();
}

module mobilePhone() {
  translate([-0.4,0,11]) rotate([0,90,0]) mobilePhoneImpl(7.7,14.8,0.7,1);
}

module mobilePhoneHolder() {
  translate([-1.5,0,4.5]) mobilePhoneHolderFront();
  translate([1.5,0,4.5])
    rotate([0,180,0]) mirror([0,1,0]) mobilePhoneHolderFront();

  translate([-2.0,carWidth/2-0.2,4.5]) stainless() rotate([-90,0,0])
    scale([0.15,0.15,0.15]) screw("M1.6x10", thread="modeled");
  translate([2.0,carWidth/2-0.2,4.5]) stainless() rotate([-90,0,0])
    scale([0.15,0.15,0.15]) screw("M1.6x10", thread="modeled");

  translate([-2.0,-carWidth/2+0.2,4.5]) stainless() rotate([90,0,0])
    scale([0.15,0.15,0.15]) screw("M1.6x10", thread="modeled");
  translate([2.0,-carWidth/2+0.2,4.5]) stainless() rotate([90,0,0])
    scale([0.15,0.15,0.15]) screw("M1.6x10", thread="modeled");
}

module top() {
  translate([0,0,carHeight])
     difference() {
       union() {
         color("SaddleBrown",transparencyTop)
           roundedCube([carLength,carWidth,carThickness],192,360/(192*2));
         scale([0.93,0.90,1]) translate([0,0,-carThickness])
            color("SaddleBrown",transparencyTop)
              roundedCube([carLength,carWidth,carThickness],192,360/(192*2));
       }
       translate([0,0,-2*carThickness])
        color("SaddleBrown",transparencyTop) cube([1,8,3],true);
     }
 }

// modules suporte -----------------------------------------------------

module mobilePhoneHolderFront() {
  difference() {
    cube([2,carWidth-2*carThickness,1],true);
    translate([0.7,-carWidth/2+0.3,0]) cube([1,2,2],true);
    translate([0.7,carWidth/2-0.3,0]) cube([1,2,2],true);
  }
}

module frontSpeedSensor() {
  // front right
  translate([wheelDistance-1.3,-1.2/2-speedSensorWheelSideDistance-0.07,-1.4])
    photoSensor();

  // front left
  translate([wheelDistance-1.3,1.2/2 +speedSensorWheelSideDistance+0.07,-0.9])
    rotate([180,0,0]) photoSensor();
}

module motorsFront() {
  // front right
  translate([wheelDistance-motorWith/2,-carWidth/2+0.15,-motorHeight])
    metalMotor();
  // front left
  translate([wheelDistance+motorWith/2, carWidth/2-0.15,-motorHeight])
    rotate([0,0,180]) metalMotor();
}

module usFront() {
  usFrontSide();
  translate([carWidth/2-0.9 + carLength, -1,3.5]) rotate([90,180,180]) us();
  mirror([1,0,0]) usFrontSide();
}

module usFrontSide() {
  translate([carLength,0,0]) rotate([0,0,60]) translate([carWidth/2-0.9,-1,3.5])
    rotate([90,180,180]) us();
  translate([carLength,0,0]) rotate([0,0,30]) translate([carWidth/2-0.9,-1,3.5])
    rotate([90,180,180]) us();
}

module hollowRoundedCube(dimensions = [10,10,10], faces=12, thickness = 1, transparency = 1.0, rotation = 360 /(12 * 2.0)) {
  difference() {
    color("DarkKhaki",transparency) roundedCube(dimensions,faces,rotation);
    translate([0,0,thickness/2.0]) color("Khaki",transparency)
      roundedCube([dimensions[0],dimensions[1]-thickness,dimensions[2]],faces,rotation);
  }
}

module roundedCube(dimensions = [10,10,10], faces=12, rotation = 360 /(12 * 2.0) ) {
  hull() {
     translate([-dimensions[0],0,0])
      rotate([0, 0, rotation])
      cylinder(h=dimensions[2],r=dimensions[1]/2.0,center=false,$fn=faces);
     translate([ dimensions[0],0,0])
      rotate([0, 0, rotation])
      cylinder(h=dimensions[2],r=dimensions[1]/2.0,center=false,$fn=faces);
  }
}

module mobilePhoneImpl(with, height,tickness,roundRadio) {
   union() {
     color("DarkSlateGray") roundedBox(with, height,tickness,roundRadio);
     translate([-height/2+2,0,0.1]) {
         difference() {
           color("Silver") roundedBox(1.5, 1.5,tickness,0.5);
           color("DarkSlateGray") roundedBox(1.3, 1.3,tickness+1,0.45);
         }
         color("White") cylinder(h=tickness,r=0.2,center=false,$fn=120);
     }
     translate([-height/2+3.3,0,0.1])
         color("White") roundedBox(0.5, 0.5,tickness+0.01,0.03);
   }
}

module roundedBox(with, height,tickness,roundRadio) {
   hull() {
     translate([-height/2+roundRadio,-with/2+roundRadio,0])
       cylinder(h=tickness,r=roundRadio,center=false,$fn=120);
     translate([-height/2+roundRadio,with/2-roundRadio,0])
       cylinder(h=tickness,r=roundRadio,center=false,$fn=120);
     translate([height/2-roundRadio,-with/2+roundRadio,0])
       cylinder(h=tickness,r=roundRadio,center=false,$fn=120);
     translate([height/2-roundRadio,with/2-roundRadio,0])
       cylinder(h=tickness,r=roundRadio,center=false,$fn=120);
  }
}

module wheel(diameter = 10, width = 2) {
  translate([0,width/2.0,0])
    rotate([90, 90, 0]) cylinder(h=width,r=diameter/2.0,center=false,$fn=1200);
}

module robotWheel(diameter = 10, width = 2) {
  //cylinder(h=width,r=diameter/2.0,center=false,$fn=1200);
  TC_Wheel(diameter - 0.8,width,1.2,0);
}

module us() {
  with = 4.5;
  height = 2;
  depthBase = 0.1;

  diameter = 1.5;
  depth = 1;
  margin = 0.1;
  translate ([0,0,height/2.0]) rotate([90, 0, 90]) {
    translate([-with/2.0,-height/2.0,0])
      color("Indigo") cube([with,height,depthBase],false);

    translate([-with/2.0 + margin + diameter/2.0,0,depthBase])
      usCylinder(depth,diameter,margin);
    translate([ with/2.0 - margin - diameter/2.0,0,depthBase])
      usCylinder(depth,diameter,margin);

    translate([0,height/2.0 - 0.3,depthBase])
      color("Silver") roundedCube([0.5,0.4,0.3],120,0);
  }
}

module usCylinder(depth,diameter,margin) {
  difference() {
    color("Silver")
      cylinder(h=depth,r=diameter/2.0,center=false,$fn=120);
    translate([0,0,depth-margin])
      color("DarkSlateGray")
        cylinder(h=depth,r=diameter/2.0 - margin,center=false,$fn=120);
  }
}

module metalMotor() {
   color("Silver")
    cube([motorWith,motorLength,motorHeight],false);
    translate([motorWith/2,0 ,(motorHeight + axisRadius)/2])
    rotate([90,0,0])
    color("White")
    cylinder(h=axisLength,r=axisRadius,center=false,$fn=120);
}

module bateryHolder2Sides10xAA() {
   bateryLength = 7.5;
   bateryWith = 6;
   bateryHeight = 3;

   color("Black") cube([bateryLength,bateryWith,bateryHeight],false);
}

module bateryHolder2Sides6xAA() {
   bateryLength = 5.8;
   bateryWith = 4.8;
   bateryHeight = 2.8;

   color("Black") cube([bateryLength,bateryWith,bateryHeight],false);
}

module photoSensor() {
  with = 1.2;
  height = 1;
  depth = 0.5;

  withBase = 0.4;
  heightBase = 0.1;

  withHoole = 0.5;
  heightHoole = height;
  depthHole = 0.4;
  diameterHoole = 0.5;
  union() color("Black") {
   translate([withHoole/2,with-depthHole,heightHoole-withHoole/2])
     rotate([0,90,90]) difference() {
       hull() {
         cylinder(h=depthHole,r=withHoole/2,center=false,$fn=120);
         translate([heightHoole,0,0])
           cylinder(h=depthHole,r=withHoole/2,center=false,$fn=120);
       }
       translate([0,0,-0.2])
         cylinder(h=depthHole+1,r=withHoole/2-0.1,center=false,$fn=120);
       translate([heightHoole,0,-0.2])
         cylinder(h=depthHole+1,r=withHoole/2-0.1,center=false,$fn=120);
     }
     difference() {
       cube([height,with,depth]);
       translate([heightBase,(with-withBase)/2,-1])
         cube([height,withBase,depth+2]);
     }
  }
}

module TC_Wheel(wd,ww,hd,off) {

  module Hex_Prism(d,h) {
  	difference() {
  		cylinder (r=1*d,h=h,center=true);
  		union() {
  			for (i=[0:1:5]) {
  				rotate([0,0,i*60]) translate([d,0,0]) cube([d,1.1*d,1.01*h], center=true);
  			}
  		}
  	}
  }
  
  module TC_Wheel_Tire() {
		olt = 0.2;	//Outer Lip Thickness

		translate([0,0,-ww/2 + 0.02])
		union() {
			color("DimGray") cylinder(r=wd/2+0.4,h=ww-0.03,center=false);
		}
	}

	module TC_Wheel_Tire_Groove() {
		olt = 0.2;	// Outer Lip Thickness
		ilt = 0.1;	// Inner Lip Thickness
		gap = 0.4;	// Gap for the tire bead

		translate([0,0,-ww/2])
		union() {
			cylinder(r=wd/2+0.2,h=olt);
			translate([0,0,gap+olt]) cylinder(r=wd/2+0.1,h=ilt);
		}
	}

	module TC_Wheel_Center() {
		dht = 0.4;  	// Drive Hex Thickeness
		wcd = 0.10;  	// Clearance Diameter for the Axle Nut Wrench
		nt  = 0.4;	  // Axle Nut Thickness
		ad  = 0.4;		// Axle Diameter
		d   = ww/2 - off - ins;

		translate([0,0,(ww-d)/2-ins])
		difference() {
			cylinder(r=cd/2, h=d,center=true);
			cylinder(r=ad/2,h=d+0.1, center=true);
			translate([0,0,-d/2+dht/2-.001]) Hex_Prism(hd,dht);
			translate([0,0,d/2-nt/2+.01]) cylinder (r=wcd/2,h=nt,center=true);
		}
	}

	module TC_Wheel_Spokes(n) {
		ra = 360/n;		  	// Radial Angle
		st = 0.1;			  	// Spoke Thickness
		sw = 0.3;			    // Spoke Width
		rh = 0.6;			  	// Rib Height
		rt = 0.1;			  	// Rib Thickness
		x  = (wd-cd)/2;
		sa = atan(ins/x);	// Spoke Angle
		sl = sqrt(x*x+ins*ins)+ins; 	// Diagonal length of spoke

		translate([0,0,ww/2-ins])
		union() {
			for (i=[0.1:0.1:n]) {
				rotate([0,0,i*ra]) translate([(hd+sl)/2-ins+0.55,0,0]) rotate([0,-sa,0]) union() {
					translate([0,0,st/2]) cube([sl,sw,st],center=true);  //Spoke
					translate([0,0,(st-rh)/2]) cube([sl,rt,rh],center=true);  //Rib
				}
			}
		}
	}

	ins = 0.3;	  // Inset from the outside face of the rim
	cd  = wd/2.5;	// Center Diameter
	rt  = 0.2;		// Rim Thickness

	union() {
		difference() {
			union() {
        TC_Wheel_Tire();
				cylinder(r=wd/2,h=ww,center=true);
				TC_Wheel_Tire_Groove();
				mirror([0,0,1]) TC_Wheel_Tire_Groove();
			}
			cylinder(r=(wd-rt)/2,h=ww+.01,center=true);
		}
		
		TC_Wheel_Center();
		intersection() {
			TC_Wheel_Spokes(1.0);
			cylinder(r=(wd-rt/2)/2,h=ww+.01,center=true);
		}
	}
}

$fs = 0.1;
$fa = 3;
