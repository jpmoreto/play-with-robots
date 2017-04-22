#include <Pid1.h>

Pid1::Pid1():
 sp(0), kp(100), ki(100), kd(100), scale(100), coMin(-255), coMax(255),
 intSum(0), lastPv(0)
{
}

void Pid1::setSp(int sp) { this->sp = sp; }
void Pid1::setKp(long kp) { this->kp = kp; }
void Pid1::setKi(long ki) { this->ki = ki; }
void Pid1::setKd(long kd) { this->kd = kd; }
void Pid1::setMultScale(long scale) { this->scale = scale; }

void Pid1::setCoMin(int coMin) { this->coMin = coMin; }
void Pid1::setCoMax(int coMax) { this->coMax = coMax; }

int Pid1::nextCo(int pv) {
  // assert: all the parameters have been defined
  // assert: this method is called periodically always with the same time interval

  int error  = sp - pv;

  return normalizeCo( kp * error + ki * calculateIntSum(error) - kd * calculateDiffPv(pv) );
}

long Pid1::calculateIntSum(int error) {
  intSum += error;

  //if(intSum > coMax) intSum = coMax;
  //else if(intSum < coMin) intSum = coMin;
  return intSum;
}

int Pid1::calculateDiffPv(int pv) {
  int diffPv = pv - lastPv;
  lastPv = pv;

  return diffPv;
}

int Pid1::normalizeCo(long coOrig) {
  int co = coOrig / scale;

  if(co > coMax) return coMax;
  if(co < coMin) return coMin;
  return co;
}
