#ifndef PID1_H
#define PID1_H
/*
http://controlguru.com/motivation-and-terminology-of-automatic-process-control
https://github.com/br3ttb/Arduino-PID-Library

co = co_bias + Kc * e(t) + (Kc / Ti) * Integral(e(t)) + Kc * Td * Diferencial(e(t))

simplificando

co = Kp * e(t) + Ki * Integral(e(t)) + Kd * Diferencial(e(t))

<=>

co = Kp * e(t) + Ki * Integral(e(t)) - Kd * Diferencial(pv)

*/

class Pid1 {

public:
  Pid1();

  void setSp(int sp); // set set point
  void setKp(long kp); // set proporcional multiplier factor;
  void setKi(long ki); // set integral multiplier factor;
  void setKd(long kd); // set diferencial multiplier factor;
  void setMultScale(long scale); // the unit of multipliers are multiplier / scale
                                // if scale = 100, a value of kd = 10 means 0.1

  void setCoMin(int coMin); // control output min value
  void setCoMax(int coMax); // control output min value

  int nextCo(int pv); // returns the next control output value; pv is the process variable.

private:
  int sp;
  long kp;
  long ki;
  long kd;
  long scale;
  int coMin;
  int coMax;

  long intSum; // to avoid overflows with unexpected results
  int lastPv;

  long calculateIntSum(int error);
  int calculateDiffPv(int pv);
  int normalizeCo(long coOrig);
};

#endif
