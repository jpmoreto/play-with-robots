#ifndef PHOTO_SPEED_SENSOR_H
#define PHOTO_SPEED_SENSOR_H

class PhotoSpeedSensor {

public:
  PhotoSpeedSensor(int interruptPin, int ioPin);
  void setup();

  void resetSensorReads();

  int getSensorRead();
  int getDeltaSensorRead();

  // for internal use only
  void interruptHandler();

private:
  int interruptPin;
  int ioPin;
  volatile int sensorRead;
  int sensorReadBeforeReset;
  int sensorReadPrevious;
};

#endif
