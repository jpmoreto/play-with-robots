#include <UsSonars.h>
#include <Arduino.h>

UsSonars* global_us_sonars;

void usEchoCheck() { // If ping received, set the sensor distance to array.
  global_us_sonars->interruptHandler();
}

UsSonars::UsSonars(uint8_t us_000_ioPin, unsigned int max_cm_distance_):
  sonar {
    NewPing(us_000_ioPin + 0, us_000_ioPin + 0, max_cm_distance),
    NewPing(us_000_ioPin + 1, us_000_ioPin + 1, max_cm_distance),
    NewPing(us_000_ioPin + 2, us_000_ioPin + 2, max_cm_distance),
    NewPing(us_000_ioPin + 3, us_000_ioPin + 3, max_cm_distance),
    NewPing(us_000_ioPin + 4, us_000_ioPin + 4, max_cm_distance),
    NewPing(us_000_ioPin + 5, us_000_ioPin + 5, max_cm_distance),
    NewPing(us_000_ioPin + 6, us_000_ioPin + 6, max_cm_distance),
    NewPing(us_000_ioPin + 7, us_000_ioPin + 7, max_cm_distance),
    NewPing(us_000_ioPin + 8, us_000_ioPin + 8, max_cm_distance),
    NewPing(us_000_ioPin + 9, us_000_ioPin + 9, max_cm_distance),
    NewPing(us_000_ioPin + 10, us_000_ioPin + 10, max_cm_distance),
    NewPing(us_000_ioPin + 11, us_000_ioPin + 11, max_cm_distance)
  },
  current_us_sensor(0), max_cm_distance(max_cm_distance_) {

    for(int i = 0; i < us_number; ++i) {
      sonar_distances[i] = max_cm_distance;
      previous_sonar_distances[i] = max_cm_distance;
    }
    global_us_sonars = this;
}

void UsSonars::setup() {
  read_next();
}

void UsSonars::read_next() {
  sonar[current_us_sensor].timer_stop();

  ++current_us_sensor;
  if (current_us_sensor == us_number) { // one turn completed - first turn is garbage
    current_us_sensor = 0;
  }

  sonar_distances[current_us_sensor] = max_cm_distance; // Make distance 255 in case there's no ping echo for this sensor.

  sonar[current_us_sensor].ping_timer(usEchoCheck); // Do the ping (processing continues, interrupt will call usEchoCheck to look for echo).
}

void UsSonars::read(UsDistancesMessage& msg) {
  for(uint8_t i = 0; i < UsSonars::us_number; ++i) {
    msg.distances[i] = get_distance(i);
    clear_distance(i);
  }
}

unsigned int UsSonars::get_distance(uint8_t pos) {
  if(pos < us_number)
    return previous_sonar_distances[pos];

  return max_cm_distance;
}

void UsSonars::clear_distance(uint8_t pos) {
  if(pos < us_number)
    previous_sonar_distances[pos] = max_cm_distance;
}

uint8_t UsSonars::get_current_us_to_read() {
  return current_us_sensor;
}

void UsSonars::interruptHandler() {
  if (sonar[current_us_sensor].check_timer()) {
    sonar_distances[current_us_sensor] =  sonar[current_us_sensor].ping_result / US_ROUNDTRIP_CM;
    previous_sonar_distances[current_us_sensor] =  sonar_distances[current_us_sensor];
  }
}
