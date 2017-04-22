#include <Vcc.h>
#include <Arduino.h>

int Vcc::read() {
  // const long INTERNAL_1_1_VREF = 1.1L; // INTERNAL_1_1_VREF = 1.1 * Vcc1 (per
  // voltmeter) / Vcc2 (per readVcc() function)
  // const long VCC_SCALE = INTERNAL_1_1_VREF * 1023 * 1000;
  // 1125300 = 1.1*1023*1000
  //
  static const long VCC_SCALE = 1125300L;

// Read 1.1V reference against AVcc
// set the reference to Vcc and the measurement to the internal 1.1V reference
#if defined(__AVR_ATmega32U4__) || defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
  ADMUX = _BV(REFS0) | _BV(MUX4) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
#elif defined(__AVR_ATtiny24__) || defined(__AVR_ATtiny44__) || defined(__AVR_ATtiny84__)
  ADMUX = _BV(MUX5) | _BV(MUX0);
#elif defined(__AVR_ATtiny25__) || defined(__AVR_ATtiny45__) || defined(__AVR_ATtiny85__)
  ADMUX = _BV(MUX3) | _BV(MUX2);
#else
  ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
#endif

  ADCSRA |= _BV(ADSC); // Start conversion
  while (bit_is_set(ADCSRA, ADSC)); // measuring

  const uint8_t low  = ADCL;  // must read ADCL first - it then locks ADCH
  const uint8_t high = ADCH; // unlocks both

  return (int)(VCC_SCALE / ((high << 8) | low));          // Vcc in millivolts
}
