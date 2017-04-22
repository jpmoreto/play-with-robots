#include <Arduino.h>
#include <MotorsControl.h>
#include <MeasureTimeMicros.h>
#include <Comm.h>
#include <UsSonars.h>
#include <Vcc.h>
#include <Mpu.h>

// io PINS

// Photoelectric Speed Sensor Encoder
const int SS_FRONT_LEFT_A = 2; // interrupt enable
const int SS_FRONT_LEFT_B = 35;

const int SS_FRONT_RIGHT_A = 3; // interrupt enable
const int SS_FRONT_RIGHT_B = 36;

const int SS_BACK_LEFT_A = 18; // interrupt enable
const int SS_BACK_LEFT_B = 37;

const int SS_BACK_RIGHT_A = 19; // interrupt enable
const int SS_BACK_RIGHT_B = 38;

// motor controler
const int MOTOR_PWM_FRONT_LEFT  = 4; // PWM enable
const int MOTOR_PWM_FRONT_RIGHT = 5; // PWM enable
const int MOTOR_PWM_BACK_LEFT   = 6; // PWM enable
const int MOTOR_PWM_BACK_RIGHT  = 7; // PWM enable

/* TODO: alterar diagrama de conexões do motor no Fritzing
const int MOTOR_DIRECTION_RIGHT_1 = 39;
const int MOTOR_DIRECTION_RIGHT_2 = 40;

const int MOTOR_DIRECTION_LEFT_1 = 41;
const int MOTOR_DIRECTION_LEFT_2 = 42;

const int MOTOR_STBY = 43;
*/
const int MOTOR_DIRECTION_FRONT_RIGHT_1 = 39;
const int MOTOR_DIRECTION_FRONT_RIGHT_2 = 40;

const int MOTOR_DIRECTION_FRONT_LEFT_1 = 41;
const int MOTOR_DIRECTION_FRONT_LEFT_2 = 42;

const int MOTOR_DIRECTION_BACK_RIGHT_1 = 43;
const int MOTOR_DIRECTION_BACK_RIGHT_2 = 44;

const int MOTOR_DIRECTION_BACK_LEFT_1 = 45;
const int MOTOR_DIRECTION_BACK_LEFT_2 = 46;

const int MOTOR_STBY = 47;


// Bluetooth TODO: alterar diagrama de conexões do Bluetooth no Fritzing
/*
const int BLUETOOTH_RX = 11;
const int BLUETOOTH_TX = 10;

const int BLUETOOTH_RX = 17; // use this <=> Serial2 in arduino mega
const int BLUETOOTH_TX = 16;
*/
// US sonars
const int US_000 = 23; // use pins from 23 to 34 inclusive

// end
// io PINS

const int BLUETOOTH_SPEED = 38400;

const long TIMER_SLEEP_TIME_MICROS = 50000;

PhotoSpeedSensor frontRightSpeedSensor(SS_FRONT_RIGHT_A, SS_FRONT_RIGHT_B);
PhotoSpeedSensor frontLeftSpeedSensor (SS_FRONT_LEFT_A, SS_FRONT_LEFT_B);
PhotoSpeedSensor backRightSpeedSensor (SS_BACK_RIGHT_A, SS_BACK_RIGHT_B);
PhotoSpeedSensor backLeftSpeedSensor  (SS_BACK_LEFT_A, SS_BACK_LEFT_B);

Motor frontRightMotor(MOTOR_STBY,MOTOR_PWM_FRONT_RIGHT,
  MOTOR_DIRECTION_FRONT_RIGHT_1,MOTOR_DIRECTION_FRONT_RIGHT_2,
  frontRightSpeedSensor);

Motor frontLeftMotor(MOTOR_STBY,MOTOR_PWM_FRONT_LEFT,
    MOTOR_DIRECTION_FRONT_LEFT_1,MOTOR_DIRECTION_FRONT_LEFT_2,
    frontLeftSpeedSensor);

Motor backRightMotor(MOTOR_STBY,MOTOR_PWM_BACK_RIGHT,
    MOTOR_DIRECTION_BACK_RIGHT_1,MOTOR_DIRECTION_BACK_RIGHT_2,
    backRightSpeedSensor);

Motor backLeftMotor(MOTOR_STBY,MOTOR_PWM_BACK_LEFT,
    MOTOR_DIRECTION_BACK_LEFT_1,MOTOR_DIRECTION_BACK_LEFT_2,
    backLeftSpeedSensor);

MotorsControl motorsControl(frontRightMotor,frontLeftMotor,backRightMotor,backLeftMotor);

MeasureTimeMicros timerControl(TIMER_SLEEP_TIME_MICROS);

Comm comm(BLUETOOTH_SPEED,Serial2);

const int US_MAX_DISTANCE_CM = 200; // cm

UsSonars usSonars(US_000,US_MAX_DISTANCE_CM);

Mpu mpu;

unsigned long actualTimeMicros = 0;

// operations periodicity defined in terms of time ticks (1 time tick = sleep time)
// sleep time == 50000 => 20 time tiks/second
//
const unsigned int TIKS_READ_COMMANDS     = 10; // 10 * TIME_TICK ms
const unsigned int TIKS_CONTROL_ACTUATORS = 1;
const unsigned int TIKS_READ_US_SENSOR    = 1;
const unsigned int TIKS_READ_MPU          = 1;
const unsigned int TIKS_READ_VCC          = 1000;

void tryCall(unsigned int &ticksCount, unsigned int maxTicks, void (*func)(void)) {

  ++ticksCount;
  if (ticksCount == maxTicks) {
    ticksCount = 0;
    func();
  }
}

void tryReadAndProcessCommands() {
  RecMessage message;

  while(comm.readMessage(message)) {
    switch(message.messageType) {
      case SetMotorsSpeed:
        motorsControl.setLeftSpeed(message.body.motorsSpeed.leftSpeed);
        motorsControl.setRightSpeed(message.body.motorsSpeed.rightSpeed);
        break;
      case CalibrateCompass:
        float factoryCompassCalibration[3];
        float compassBias[3];
        float compassScale[3];
        mpu.calibrateCompass(factoryCompassCalibration, compassBias, compassScale);

        MpuCompassCalibrationMessage msg;
        msg.timeStamp = millis();
        sprintf(&(msg.compassBias[0]), "%f;%f;%f",
            static_cast<double>(compassBias[0]),
            static_cast<double>(compassBias[1]),
            static_cast<double>(compassBias[2]));
        sprintf(&(msg.compassScale[0]), "%f;%f;%f",
            static_cast<double>(compassScale[0]),
            static_cast<double>(compassScale[1]),
            static_cast<double>(compassScale[2]));
        sprintf(&(msg.factoryCompassCalibration[0]), "%f;%f;%f",
            static_cast<double>(factoryCompassCalibration[0]),
            static_cast<double>(factoryCompassCalibration[1]),
            static_cast<double>(factoryCompassCalibration[2]));
        comm.writeMessage(msg);
        break;
      default:
        break;
    }
  }
}

void tryControlActuators() {
  motorsControl.controlSpeed();
}

void tryReadUsSensors() {
  usSonars.read_next();
  if(usSonars.get_current_us_to_read() == 0) {
    // send sensors read values
    UsDistancesMessage msg;
    msg.timeStamp = millis();
    usSonars.read(msg);
    comm.writeMessage(msg);
  }
}

void tryReadVcc() {
  VccMessage msg;
  msg.timeStamp = millis();
  msg.vcc = Vcc::read();
  comm.writeMessage(msg);
}

void tryReadMpuSensors() {
  MpuSensorsValuesMessage msg;

  if(mpu.read(msg))
  {
    msg.timeStamp = millis();
    comm.writeMessage(msg);
  }
}
#ifndef UNIT_TEST
// setup and loop
// ***************************************************************
void setup() {
  motorsControl.setMultScale(100);
  motorsControl.setKd(100);
  motorsControl.setKi(100);
  motorsControl.setKd(100);
  motorsControl.setLoopPeriod(TIMER_SLEEP_TIME_MICROS / 1000);
  motorsControl.setup();

  comm.setup();
  usSonars.setup();
  timerControl.setup();
  mpu.setup();
}

void loop() {

  static unsigned int ticksReadCommandsCount = 0;
  static unsigned int ticksControlActuatorsCount = 0;
  static unsigned int ticksReadUsSensor = 0;
  static unsigned int ticksReadMpuSensor = 0;
  static unsigned int ticksReadVCC = 0;

  timerControl.sleep();

  actualTimeMicros = micros();
  /*const unsigned long deltaTime = */timerControl.getDeltaTime(actualTimeMicros);

  tryCall(ticksControlActuatorsCount, TIKS_CONTROL_ACTUATORS, tryControlActuators);
  tryCall(ticksReadCommandsCount,     TIKS_READ_COMMANDS,     tryReadAndProcessCommands);
  tryCall(ticksReadMpuSensor,         TIKS_READ_MPU,          tryReadMpuSensors);
  tryCall(ticksReadUsSensor,          TIKS_READ_US_SENSOR,    tryReadUsSensors);
  tryCall(ticksReadVCC,               TIKS_READ_VCC,          tryReadVcc);
}
#endif
