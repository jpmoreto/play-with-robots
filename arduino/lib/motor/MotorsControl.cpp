#include <MotorsControl.h>
#include <Arduino.h>

MotorsControl::MotorsControl(
  Motor frontRightMotor_,
  Motor frontLeftMotor_,
  Motor backRightMotor_,
  Motor backLeftMotor_):
      frontRightMotor(frontRightMotor_),
      frontLeftMotor(frontLeftMotor_),
      backRightMotor(backRightMotor_),
      backLeftMotor(backLeftMotor_),
      motors {&frontRightMotor,&frontLeftMotor,&backRightMotor,&backLeftMotor},
      isStandby(false)
      {}

void MotorsControl::setup() {
  setStandby(true);
  for(int i = 0; i < 4; ++i) motors[i]->setup();
}

void MotorsControl::setLoopPeriod(int period) {
  loopPeriod = period;
}

void MotorsControl::setKp(long kp) {
  for(int i = 0; i < 4; ++i) motors[i]->controler.setKp(kp);
}

void MotorsControl::setKi(long ki) {
  for(int i = 0; i < 4; ++i) motors[i]->controler.setKi(ki);
}

void MotorsControl::setKd(long kd) {
  for(int i = 0; i < 4; ++i) motors[i]->controler.setKd(kd);
}

void MotorsControl::setMultScale(long scale) {
  for(int i = 0; i < 4; ++i) motors[i]->controler.setMultScale(scale);
}

int MotorsControl::getSpeed(int speed) {
  return speed * loopPeriod / 1000;
}

void MotorsControl::setLeftSpeed(int leftSpeed) {
  int speed = getSpeed(leftSpeed);
  frontLeftMotor.setSpeed(speed);
  backLeftMotor.setSpeed(speed);
}

void MotorsControl::setRightSpeed(int rightSpeed) {
  int speed = getSpeed(rightSpeed);
  frontRightMotor.setSpeed(speed);
  backRightMotor.setSpeed(speed);
}

void MotorsControl::setStandby(const bool standby) {
  frontRightMotor.setStandby(standby); // standby pin is shared by all motors
  isStandby = standby;
}

void MotorsControl::resetSensorReads() {
  for(int i = 0; i < 4; ++i) motors[i]->sensor.resetSensorReads();
}

void MotorsControl::controlSpeed() {

  noInterrupts();
  int deltaSpeedSensors[4];
  for(int i = 0; i < 4; ++i) deltaSpeedSensors[i] = motors[i]->sensor.getDeltaSensorRead();
  this->resetSensorReads();
  interrupts();

  if(isStandby) setStandby(false);
  for(int i = 0; i < 4; ++i) motors[i]->setPower(deltaSpeedSensors[i]);
}
