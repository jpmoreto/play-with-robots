#ifndef US_SONARS_H
#define US_SONARS_H

#include <Comm.h>
#include <NewPing.h>

class UsSonars {

public:
  UsSonars(uint8_t us_000_ioPin, unsigned int max_cm_distance);
  void setup();

  void read_next();
  uint8_t get_current_us_to_read();
  void read(UsDistancesMessage& msg);

  unsigned int get_distance(uint8_t pos);
  void clear_distance(uint8_t pos);

  // for internal use only
  void interruptHandler();

  static const uint8_t us_number = 12;

private:

  NewPing sonar[us_number]; // us_000, us_030, us_060, ... , us_330

  volatile uint8_t current_us_sensor;
  unsigned int max_cm_distance;

  volatile unsigned int sonar_distances[us_number];
  volatile unsigned int previous_sonar_distances[us_number];
};

#endif
