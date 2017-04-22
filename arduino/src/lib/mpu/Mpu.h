#ifndef MPU_H
#define MPU_H
#include <Comm.h>
#include <MPU9250.h>

// see:
// https://github.com/sparkfun/SparkFun_MPU-9250-DMP_Arduino_Library
// https://github.com/sparkfun/MPU-9250_Breakout
// https://github.com/sparkfun/SparkFun_MPU-9250_Breakout_Arduino_Library ***
// https://www.hackster.io/paulplusx/using-the-mpu9250-to-get-real-time-motion-data-08f011
// https://github.com/kriswiner/MPU-9250/issues/57
// https://github.com/kriswiner/MPU-9250
//
// https://groups.yahoo.com/neo/groups/Homebrew_PCBs/info
// https://www.youtube.com/watch?v=wVsfCnyt5jA&spfreload=10
// https://www.youtube.com/watch?v=lo8MWr3NuuM
// https://github.com/loopj/i2c-sensor-hal
// https://www.youtube.com/channel/UCT5e-XjqHPfA3_9wF3CgY1w

class Mpu {

public:
  Mpu();
  void setup();

  bool read(MpuSensorsValuesMessage& message);
  void calibrateCompass(float* factoryCompassCalibration, float* compassBias, float* compassScale);
private:
   MPU9250 myIMU;
};

#endif
