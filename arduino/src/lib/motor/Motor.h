#ifndef MOTOR_H
#define MOTOR_H

#include <PhotoSpeedSendor.h>
#include <Pid1.h>

class Motor {

public:
  Motor(int STBYIoPin, int pwmIoPin, int in1IoPin, int in2IoPin, PhotoSpeedSensor sensor);
  void setup();

  void setPower(int pv);
  int getPower();
  void setStandby(bool standby);
  bool isStandby();
  void runBackward();
  void runForward();
  void stop();

  void setSpeed(int speed);
  Pid1 controler;
  PhotoSpeedSensor sensor;

  static const int maxPower = 255;
  static const int maxIncrementPower = 5;

private:
  const int STBYIoPin;
  const int pwmIoPin;
  const int in1IoPin;
  const int in2IoPin;

  bool standby;
  int lastPower;
  int direction; // -1 => backward; 0 => stoped; 1 => forward
};

#endif
