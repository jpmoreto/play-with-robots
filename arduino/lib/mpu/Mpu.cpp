#include <Mpu.h>
#include <Wire.h>

Mpu::Mpu()
{
}

void Mpu::setup() {
  Wire.begin();
  // Read the WHO_AM_I register, this is a good test of communication
  byte c = myIMU.readByte(MPU9250_ADDRESS, WHO_AM_I_MPU9250);

  if(c == 0x71) // ok
  {
    // Start by performing self test
    myIMU.MPU9250SelfTest(myIMU.SelfTest);

    // Calibrate gyro and accelerometers, load biases in bias registers
    myIMU.calibrateMPU9250(myIMU.gyroBias, myIMU.accelBias);

    // Initialize device for active mode read of acclerometer, gyroscope, and
    // temperature
    myIMU.initMPU9250();

    // Read the WHO_AM_I register of the magnetometer, this is a good test of
    // communication
    byte d = myIMU.readByte(AK8963_ADDRESS, WHO_AM_I_AK8963);
    if(d == 0x48) // ok
    {
      // Get magnetometer calibration from AK8963 ROM
      // and initialize device for active mode read of magnetometer
      myIMU.initAK8963(myIMU.factoryMagCalibration);
    }
    // Get sensor resolutions, only need to do this once
    myIMU.getAres();
    myIMU.getGres();
    myIMU.getMres();
  }
}

void Mpu::calibrateCompass(float* factoryCompassCalibration, float* compassBias, float* compassScale) {
  // The next call delays for 4 seconds, and then records about 15 seconds of
  // data to calculate bias and scale.
  myIMU.magCalMPU9250(myIMU.magBias, myIMU.magScale);

  for(int i = 0; i < 3; ++i) {
    factoryCompassCalibration[i] = myIMU.factoryMagCalibration[i];
    compassBias[i] = myIMU.magBias[i];
    compassScale[i] = myIMU.magScale[i];
  }
}
bool Mpu::read(MpuSensorsValuesMessage& message) {

  // check if data ready interrupt
  bool sucess = myIMU.readByte(MPU9250_ADDRESS, INT_STATUS) & 0x01;

  if (sucess)
  {
    myIMU.readAccelData(myIMU.accelCount);  // Read the x/y/z adc values
    myIMU.readGyroData(myIMU.gyroCount);  // Read the x/y/z adc values
    myIMU.readMagData(myIMU.magCount);  // Read the x/y/z adc values
    myIMU.tempCount = myIMU.readTempData();  // Read temp

    for(int i = 0; i < 3; ++i)
    {
      message.accelerometer[i] = myIMU.accelCount[i];
      message.gyroscope[i] = myIMU.gyroCount[i];
      message.compass[i] = myIMU.magCount[i];
    }
    message.temperature = myIMU.tempCount;
  }
  return sucess;
}
