#include <Comm.h>
#include <Arduino.h>
//http://forum.arduino.cc/index.php?topic=168200.0

Comm::Comm(const int bluetoothSpeed_, HardwareSerial& serial_):
  bluetoothSpeed(bluetoothSpeed_), serial(serial_) {
}

void Comm::setup() {
  serial.begin(bluetoothSpeed);
}

bool Comm::readMessage(RecMessage& message) {
  if (serial.available() > 0) {
    uint8_t data[1];
    serial.readBytes(data, 1);

    switch (static_cast<RecMessageType>(data[0])) {
      case SetMotorsSpeed:
        return readSetMotorsSpeedMessage(message);
      case CalibrateCompass:
        message.messageType = CalibrateCompass;
        return true; // message body empty
      default:
        break;
    }
  }
  return false;
}

bool Comm::writeMessage(const MotorsSpeedMessage& message) {
  // send speed sensor message
  //
  uint8_t messageBuf[SPEED_SENSOR_MESSAGE_SIZE];

  setMessagePrefix(messageBuf,MotorsSpeed,message.timeStamp);

  setMessageInt(messageBuf,5,message.frontLeftSpeed);
  setMessageInt(messageBuf,7,message.frontRightSpeed);
  setMessageInt(messageBuf,9,message.backLeftSpeed);
  setMessageInt(messageBuf,11,message.backRightSpeed);

  bluetoothWrite(messageBuf,SPEED_SENSOR_MESSAGE_SIZE);

  return true;
}

bool Comm::writeMessage(const UsDistancesMessage& message) {
  uint8_t messageBuf[ULTRASONIC_SENSOR_MESSAGE_SIZE];

  setMessagePrefix(messageBuf,UsArrayDistances,message.timeStamp);

  for(int i = 0; i < 12; ++i) {
    setMessageInt(messageBuf,i * 2 + 5,message.distances[i]);
  }

  bluetoothWrite(messageBuf,ULTRASONIC_SENSOR_MESSAGE_SIZE);

  return true;
}

bool Comm::writeMessage(const MpuSensorsValuesMessage& message) {
  uint8_t messageBuf[MPU_ALL_RAW_SENSOR_MESSAGE_SIZE];

  setMessagePrefix(messageBuf,MpuSensorsValues,message.timeStamp);
  setMessageInt3D(messageBuf, 5, message.accelerometer);
  setMessageInt3D(messageBuf,11, message.gyroscope);
  setMessageInt3D(messageBuf,17, message.compass);
  setMessageInt(messageBuf,  23, message.temperature);

  bluetoothWrite(messageBuf,MPU_ALL_RAW_SENSOR_MESSAGE_SIZE);
  return true;
}

bool Comm::writeMessage(const VccMessage& message) {
  uint8_t messageBuf[VCC_MESSAGE_SIZE];

  setMessagePrefix(messageBuf,VccPower,message.timeStamp);
  setMessageInt(messageBuf,5,message.vcc);

  bluetoothWrite(messageBuf,VCC_MESSAGE_SIZE);
  return true;
}

bool Comm::writeMessage(const MpuCompassCalibrationMessage& message) {
  uint8_t messageBuf[256];
  int pos;

  setMessagePrefix(messageBuf,CompassCalibration,message.timeStamp);
  pos = setMessageString(messageBuf,5,message.compassBias);
  pos = setMessageString(messageBuf,pos,message.compassScale);
  pos = setMessageString(messageBuf,pos,message.factoryCompassCalibration);

  bluetoothWrite(messageBuf,pos);
  return true;
}

// support methods
//
bool Comm::readSetMotorsSpeedMessage(RecMessage& message) {

  uint8_t messageBuf[4];

  while (serial.available() < 4) ; // wait for next 4 bytes if necessary
  serial.readBytes(&(messageBuf[0]), 4);

  message.messageType = SetMotorsSpeed;
  message.body.motorsSpeed.leftSpeed    = msbLsb(messageBuf[0], messageBuf[1]);
  message.body.motorsSpeed.rightSpeed   = msbLsb(messageBuf[2], messageBuf[3]);

  return true;
}
// write nBytes from message
// returns number of bytes readed
//
void Comm::bluetoothWrite(uint8_t *message, int nBytes) {
  serial.write(message,nBytes);
}

void Comm::setMessagePrefix(uint8_t *message, uint8_t header, unsigned long timeStamp) {

  message[0] = header;

  for(int i = 0; i < 4; ++i) {
    message[i+1] = msb(timeStamp,3-i);
  }
}

int Comm::setMessageString(uint8_t *message, int pos, const char* str) {
    strcat((char*)(&(message[pos])),str);
    int strLenght = strlen(str);
    message[pos + strLenght + 1] = 0;

    return pos + strLenght + 2;
}

void Comm::setMessageInt(uint8_t *message, int pos, int value) {
    message[pos]     = msb(value);
    message[pos + 1] = lsb(value);
}

void Comm::setMessageInt3D(uint8_t *message, int pos, const int16_t value[]) {
  for(int i = 0; i < 3; ++i) {
    setMessageInt(message, i*2 + pos, value[i]);
  }
}

int16_t Comm::msbLsb(uint8_t msb, uint8_t lsb) { return ((int16_t)msb << 8) | lsb; }

uint8_t Comm::msb(uint32_t val, int pos) { return (val >> (pos * 8)) & 0xFF; }

uint8_t Comm::msb(int32_t val) { return (val >> 8) & 0xFF; }
uint8_t Comm::lsb(int32_t val) { return val & 0xFF; }

uint8_t Comm::msb(int16_t val) { return (val >> 8) & 0xFF; }
uint8_t Comm::lsb(int16_t val) { return val & 0xFF; }
