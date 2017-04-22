#include <PhotoSpeedSendor.h>
#include <Arduino.h>

PhotoSpeedSensor* interruptMap[6];

void interruptHandler0() {
  interruptMap[0]->interruptHandler();
}

void interruptHandler1() {
  interruptMap[1]->interruptHandler();
}

void interruptHandler2() {
  interruptMap[2]->interruptHandler();
}

void interruptHandler3() {
  interruptMap[3]->interruptHandler();
}

void interruptHandler4() {
  interruptMap[4]->interruptHandler();
}

void interruptHandler5() {
  interruptMap[5]->interruptHandler();
}

void (* interruptHandlers[6])() =
  { interruptHandler0,interruptHandler1,interruptHandler2,
    interruptHandler3,interruptHandler4,interruptHandler5 };

PhotoSpeedSensor::PhotoSpeedSensor(int interruptPin_, int ioPin_):
  interruptPin(interruptPin_),ioPin(ioPin_),sensorRead(0),sensorReadBeforeReset(0)  {
}

void PhotoSpeedSensor::setup() {
  pinMode(interruptPin, INPUT);
  pinMode(ioPin,        INPUT);

  const int interruptNumber = digitalPinToInterrupt(interruptPin);
  interruptMap[interruptNumber] = this;
  attachInterrupt (interruptNumber, interruptHandlers[interruptNumber], FALLING);
}

void PhotoSpeedSensor::interruptHandler() {
  if(digitalRead(ioPin) == HIGH)
     ++this->sensorRead;
  else
    --this->sensorRead;
}

void PhotoSpeedSensor::resetSensorReads() {
  this->sensorReadBeforeReset = this->sensorRead;
  this->sensorRead = 0;
}

int PhotoSpeedSensor::getSensorRead() {
  return this->sensorRead;
}

int PhotoSpeedSensor::getDeltaSensorRead() {

  const int delta = this->sensorRead + this->sensorReadBeforeReset - this->sensorReadPrevious;

  this->sensorReadBeforeReset = 0;
  this->sensorReadPrevious = this->sensorRead;

  return delta;
}
