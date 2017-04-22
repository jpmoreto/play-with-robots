#ifndef MEASURE_TIME_MICROS_H
#define MEASURE_TIME_MICROS_H

class MeasureTimeMicros {

public:
  MeasureTimeMicros(const unsigned int sleepTime);

  void setup();
  unsigned long getDeltaTime(const unsigned long actualTimeMicros);
  void sleep();

private:
  unsigned long previousTime;
  const unsigned int sleepTime;
  int sleepTimeError;
  bool isFirstTime;
};

#endif
