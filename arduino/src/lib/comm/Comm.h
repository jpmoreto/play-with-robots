#ifndef COMM_H
#define COMM_H
#include <Arduino.h>

// baud rate calculation
// http://wormfood.net/avrbaudcalc.php?bitrate=9600%2C19200%2C38400%2C57600%2C115200%2C460800%2C921600%2C1382400&clock=16&databits=8

/* messages formats:

A message is a fix size sequence of bytes with a header byte that identifies de
message type (and also the rest of the format and size).

Sensor reading messsages: <header> <time stamp> <sensor values>
where: <header> - 1 byte
       <time stamp> - 4 bytes (more significative bytes to the left)
       <sensor values> - list of values. The values and format of each depends on the message type.


Speed Sensor message:
4 <time stamp> <frontLeft> <frontRight> <backLeft> <backRight>

<frontLeft> <frontRight> <backLeft> <backRight> signed 2 bytes raw values, must be converted by the receiver

Speed Sensor reset message:
5 <time stamp> <frontLeft> <frontRight> <backLeft> <backRight>

<frontLeft> <frontRight> <backLeft> <backRight> signed 2 bytes raw values, must be converted by the receiver

Ultrasonic sensor message:
6 <time stamp> <us_000> <us_030> <us_060> <us_090> <us_120> <us_150> <us_180> <us_210> <us_240> <us_270> <us_300> <us_330>

 <us_000> <us_030> <us_060> <us_090> <us_120> <us_150> <us_180> <us_210> <us_240> <us_270> <us_300> <us_330> unsigned 2 bytes raw values, must be converted by the receiver

VCC voltage message:
7 <time stamp> <voltage>

<voltage> unsigned 2 bytes in milivolts

Accel + Gyro + Mag + Temp message:
8 <time stamp>
<accel_x> <accel_y> <accel_z> // 2 bytes each. signed accelaration raw.
<gyro_x> <gyro_y> <gyro_z>    // 2 bytes each. signed angular velocity raw.
<mag_x> <mag_y> <mag_z>       // 2 bytes each. signed magnetic field raw.
<temp>                        // 2 bytes. unsigned Temperature raw.

Command messages: <header> <values>
where:  <header> - 1 byte
        <command values> - list of values. The values and format of each depends on the message type.

Set speed message: 101 <left> <right> <acc>
   <left> <right> signed raw values. Each value have 2 bytes: HSB LSB. Motors of the same side have the same velocity

Multi message Message: <header> <time stamp> <number of messages> <messages
without time stamp>
where: <header> - 1 byte value is 0
       <time stamp> - 4 bytes (more significative bytes to the left). All the
messages share the same <time stamp>
       <number of messages> - 1 byte. Number of following messages.
       <messages without time stamp> - list of messages as previous described,
but without <time stamp>

*/

// Types of send messages
//
enum SendMessageType {
  MotorsSpeed = 1,
  UsArrayDistances = 2,
  MpuSensorsValues = 3,
  VccPower = 4,
  CompassCalibration = 5
};

typedef struct
{
  unsigned long timeStamp;

  int frontLeftSpeed;
  int frontRightSpeed;
  int backLeftSpeed;
  int backRightSpeed;
} MotorsSpeedMessage;

typedef struct
{
  unsigned long timeStamp;

  int distances[12];
} UsDistancesMessage;

typedef struct
{
  unsigned long timeStamp;

  int accelerometer[3]; // { x,y,z }
  int gyroscope[3];    // { x,y,z }
  int compass[3];      // { x,y,z }
  int temperature;
} MpuSensorsValuesMessage;

typedef struct
{
  unsigned long timeStamp;

  char compassBias[64]; // "x;y;z" float format
  char compassScale[64]; // "x;y;z" float format
  char factoryCompassCalibration[64];    // "x;y;z" float format
} MpuCompassCalibrationMessage;

typedef struct
{
  unsigned long timeStamp;

  int vcc;
} VccMessage;

// Types of receive messages
//
enum RecMessageType {
  SetMotorsSpeed = 1,
  CalibrateCompass = 2
};

typedef struct
{
   int leftSpeed;
   int rightSpeed;
} SetMotorsSpeedMessage;

typedef struct
{
} CalibrateCompassMessage;

typedef union {
  SetMotorsSpeedMessage motorsSpeed;
  CalibrateCompassMessage calibrateCompass;
} RecMessageBody;

typedef struct
{
   RecMessageType messageType;
   RecMessageBody body;
} RecMessage;

class Comm {

public:
  Comm(const int bluetoothSpeed, HardwareSerial& serial);
  void setup();

  bool readMessage(RecMessage& message);
  bool writeMessage(const MotorsSpeedMessage& message);
  bool writeMessage(const UsDistancesMessage& message);
  bool writeMessage(const MpuSensorsValuesMessage& message);
  bool writeMessage(const VccMessage& message);
  bool writeMessage(const MpuCompassCalibrationMessage& message);
private:
  const int bluetoothSpeed;
  HardwareSerial& serial;

  bool readSetMotorsSpeedMessage(RecMessage& message);

  void bluetoothWrite(uint8_t *message, int nBytes);
  void setMessagePrefix(uint8_t *message, uint8_t header, unsigned long timeStamp);
  void setMessageInt(uint8_t *message, int pos, int value);
  void setMessageInt3D(uint8_t *message, int pos, const int16_t value[]);
  int setMessageString(uint8_t *message, int pos, const char* str);
  int16_t msbLsb(uint8_t msb, uint8_t lsb);
  uint8_t msb(uint32_t val, int pos);
  uint8_t msb(int32_t val);
  uint8_t lsb(int32_t val);
  uint8_t msb(int16_t val);
  uint8_t lsb(int16_t val);

  static const int MAX_MESSAGE_SIZE = 7;

  // Rec messages
  //
  static const int SET_SPEED_MESSAGE_SIZE = 1 + 2 * 2;

  // Send messages
  //
  // frequency: 1 message per second?  => 13 * 1 bytes/s
  static const int SPEED_SENSOR_MESSAGE_SIZE = 5 + 4 * 2;

  // frequency: 5 message per second?  => 29 * 5 bytes/s
  static const int ULTRASONIC_SENSOR_MESSAGE_SIZE = 5 + 12 * 2;

  // frequency: 1 message per minute?  => 7 * (1/60) bytes/s
  static const int VCC_MESSAGE_SIZE = 5 + 1 * 2;

  // frequency: 30 message per second? => 25 * 30 bytes/s
  static const int MPU_ALL_RAW_SENSOR_MESSAGE_SIZE = 5 + 10 * 2;

  // tot bandwith send = 13 * 1 + 29 * 5 + 7 * (1/60.0) + 25 * 30 bytes/s = 910 bytes/s
  // tot bandwith send = 13 * 2 + 29 * 5 + 7 * (1/60.0) + 25 * 100 bytes/s = 2671 bytes/s

  // tot bandwith rec = 5 * 5 bytes/s = 25 bytes/s
};

#endif
