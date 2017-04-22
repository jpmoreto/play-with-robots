#ifndef MOTORS_CONTROL_H
#define MOTORS_CONTROL_H

#include <PhotoSpeedSendor.h>
#include <Motor.h>

class MotorsControl {

public:
  MotorsControl(Motor frontRightMotor,Motor frontLeftMotor,Motor backRightMotor,Motor backLeftMotor);
  void setup();

  void setLeftSpeed(int leftSpeed); // ticks per second
  void setRightSpeed(int rightSpeed); // ticks per second
  void setLoopPeriod(int period); // time interval in ms bettwen calls to controlSpeed

  void setKp(long kp); // set proporcional multiplier factor;
  void setKi(long ki); // set integral multiplier factor;
  void setKd(long kd); // set diferencial multiplier factor;
  void setMultScale(long scale); // the unit of multipliers are multiplier / scale

  void resetSensorReads();
  void controlSpeed();
  void setStandby(const bool standby);

private:
  int getSpeed(int speed);

  Motor frontRightMotor;
  Motor frontLeftMotor;
  Motor backRightMotor;
  Motor backLeftMotor;
  Motor* motors[4];

  // motorsState
  //
  bool isStandby;

  int loopPeriod;
};

#endif
