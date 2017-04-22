#include <MeasureTimeMicros.h>
#include <Arduino.h>
#include <limits.h>

MeasureTimeMicros::MeasureTimeMicros(const unsigned int sleepTime_):
 sleepTime(sleepTime_) {
  previousTime = 0;
  sleepTimeError = 0;
  isFirstTime = true;
}

void MeasureTimeMicros::setup() {
  getDeltaTime(micros());
}

unsigned long MeasureTimeMicros::getDeltaTime(const unsigned long actualTime) {

  const unsigned long delta = (actualTime > previousTime) ?
       (actualTime - previousTime) : (ULONG_MAX - previousTime + actualTime);

  if(isFirstTime == false) {
    sleepTimeError = delta - sleepTime;
  }

  previousTime = actualTime;
  isFirstTime = false;
  return delta;
}

void MeasureTimeMicros::sleep() {
  delayMicroseconds(sleepTime - sleepTimeError);
}
