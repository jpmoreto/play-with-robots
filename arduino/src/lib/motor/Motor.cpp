#include <Motor.h>
#include <Arduino.h>
#include <limits.h>

Motor::Motor(int STBYIoPin_, int pwmIoPin_, int in1IoPin_, int in2IoPin_, PhotoSpeedSensor sensor_) :
  controler(), sensor(sensor_), STBYIoPin(STBYIoPin_), pwmIoPin(pwmIoPin_), in1IoPin(in1IoPin_), in2IoPin(in2IoPin_), lastPower(0), direction(0) {
    controler.setCoMin(-maxPower);
    controler.setCoMax(maxPower);
}

void Motor::setup() {
  pinMode(STBYIoPin, OUTPUT);
  pinMode(in1IoPin, OUTPUT);
  pinMode(in2IoPin, OUTPUT);
  pinMode(pwmIoPin, OUTPUT);
  digitalWrite(pwmIoPin, LOW);
  setStandby(true);
  stop();
  sensor.setup();
}

void Motor::setPower(int pv) {
  int newPower = controler.nextCo(pv);

  // control max accelaration
  if(newPower > lastPower + maxIncrementPower)
    newPower = lastPower + maxIncrementPower;
  else if(newPower < lastPower - maxIncrementPower)
    newPower = lastPower - maxIncrementPower;

  const boolean changeDirection = newPower * lastPower < 0;

  // stop before change direction to not stress the motor
  if(changeDirection)
    newPower = 0;

  lastPower = newPower;

  if(newPower > 0) {
    // change direction if needed
    if(direction <= 0) {
      runForward();
    }
    analogWrite(pwmIoPin, newPower);
  }
  else if(newPower < 0) {
    // change direction if needed
    if(direction >= 0) {
      runBackward();
    }
    analogWrite(pwmIoPin, -newPower);
  }
  else {
    // stop if needed
    if(direction >= 0) {
      stop();
    }
    analogWrite(pwmIoPin, 0);
  }
}

int Motor::getPower() {
  return lastPower;
}

void Motor::setStandby(bool standby) {
  digitalWrite(STBYIoPin, standby? LOW : HIGH);
  this->standby = standby;
}

void Motor::setSpeed(int speed) {
  controler.setSp(speed);
}

bool Motor::isStandby() {
  return standby;
}

void Motor::runBackward() {
  digitalWrite(in1IoPin, LOW);
  digitalWrite(in2IoPin, HIGH);
  direction = -1;
}

void Motor::runForward() {
  digitalWrite(in1IoPin, HIGH);
  digitalWrite(in2IoPin, LOW);
  direction = 1;
}

void Motor::stop() {
  digitalWrite(in1IoPin, LOW);
  digitalWrite(in2IoPin, LOW);
  direction = 0;
}
