#include <Adafruit_ADS1X15.h>
#include <ArduinoBLE.h>

Adafruit_ADS1115 ads;  /* Use this for the 16-bit version */

const char* deviceServiceUuid = "00001101-0000-1000-8000-00805F9B34FB";
const char* deviceServiceCharacteristicUuid1 = "00002101-0000-1000-8000-00805F9B34FB";
const char* deviceServiceCharacteristicUuid2 = "00003101-0000-1000-8000-00805F9B34FB";
int8_t data1[16] = {0};
int8_t data2[16] = {0};
int8_t* pointer1 = data1;
int8_t* pointer2 = data2;
float arch,met5,met3,met1,heelR,heelL,hallux,toes,counter;

BLEService gaitService(deviceServiceUuid); 
BLECharacteristic gaitCharacteristic1(deviceServiceCharacteristicUuid1, BLERead | BLEWrite|BLENotify,16);
BLECharacteristic gaitCharacteristic2(deviceServiceCharacteristicUuid2, BLERead | BLEWrite|BLENotify,16);
void getReadings();

void setup(void)
{
  Serial.begin(9600);

    // Ensure serial port is ready.
  while (!Serial);

    if (!BLE.begin()) {
    Serial.println("- Starting Bluetooth® Low Energy module failed!");
    while (1);
  }
  pinMode(A0,INPUT_PULLUP);
  pinMode(A1,INPUT_PULLUP);
  pinMode(A2,INPUT_PULLUP);
  pinMode(A3,INPUT_PULLUP);
  pinMode(A6,INPUT_PULLUP);
  pinMode(A7,INPUT_PULLUP);

  BLE.setLocalName("Arduino Nano 33 BLE (Peripheral)");
  BLE.setAdvertisedService(gaitService);
  gaitService.addCharacteristic(gaitCharacteristic1);
  gaitService.addCharacteristic(gaitCharacteristic2);
  BLE.addService(gaitService);
  BLE.advertise();



  

  // The ADC input range (or gain) can be changed via the following
  // functions, but be careful never to exceed VDD +0.3V max, or to
  // exceed the upper and lower limits if you adjust the input range!
  // Setting these values incorrectly may destroy your ADC!
  //                                                                ADS1015  ADS1115
  //                                                                -------  -------
  // ads.setGain(GAIN_TWOTHIRDS);  // 2/3x gain +/- 6.144V  1 bit = 3mV      0.1875mV (default)
  // ads.setGain(GAIN_ONE);        // 1x gain   +/- 4.096V  1 bit = 2mV      0.125mV
  // ads.setGain(GAIN_TWO);        // 2x gain   +/- 2.048V  1 bit = 1mV      0.0625mV
  // ads.setGain(GAIN_FOUR);       // 4x gain   +/- 1.024V  1 bit = 0.5mV    0.03125mV
  // ads.setGain(GAIN_EIGHT);      // 8x gain   +/- 0.512V  1 bit = 0.25mV   0.015625mV
  // ads.setGain(GAIN_SIXTEEN);    // 16x gain  +/- 0.256V  1 bit = 0.125mV  0.0078125mV

  // check for error
  if (!ads.begin()) {
    Serial.println("Failed to initialize ADS.");
    while (1);
  }
}

void loop(void)
{

  //volts0 = ads.computeVolts(arch);
  //volts1 = ads.computeVolts(adc1);
  //volts2 = ads.computeVolts(adc2);
  //volts3 = ads.computeVolts(met5);

 
  //getReadings();
  Serial.print("MAC: ");
  Serial.println(BLE.address());
  BLEDevice central = BLE.central();
  Serial.println("- Discovering central device...");
  delay(500);


  if (central) {
    Serial.println("* Connected to central device!");
    Serial.print("* Device MAC address: ");
    Serial.println(central.address());
    Serial.println(" ");

    while (central.connected()) {
      getReadings();
      gaitCharacteristic1.writeValue(&data1,16,false);
      gaitCharacteristic2.writeValue(&data2,16,false);
      counter++;
      delay(500);
      }
    
    Serial.println("* Disconnected to central device!");
  }

  // delay(1000);
}

void getReadings(){
  analogReadResolution(12);

  //float volts0, volts1, volts2, volts3;

  arch = ads.readADC_SingleEnded(0);
  //adc1 = ads.readADC_SingleEnded(1);
  //adc2 = ads.readADC_SingleEnded(2);
  met5 = ads.readADC_SingleEnded(3);
  met3 = analogRead(A0);
  met1 = analogRead(A1);
  heelR = analogRead(A2);
  heelL = analogRead(A3);
  hallux = analogRead(A6);
  toes = analogRead(A7);

  arch = map(arch,0,17200,0,4096);
  met5 = map(met5,0,17200,0,4096);

  //  Serial.println("-----------------------------------------------------------");
  // Serial.print("ARCH: "); Serial.print(arch); Serial.print("  "); //Serial.print(volts0); Serial.println("V");
 
  // Serial.print("MET3: "); Serial.print(met3); Serial.println("  ");
  // Serial.print("MET5: "); Serial.print(met5); Serial.print("  "); //Serial.print(volts3); Serial.println("V");
  // Serial.print("MET1: "); Serial.print(met1); Serial.println("  ");
  // Serial.print("HEEL_R: "); Serial.print(heelR); Serial.println("  ");
  // Serial.print("HEEL_L: "); Serial.print(heelL); Serial.println("  ");
  // Serial.print("HALLUX: "); Serial.print(hallux); Serial.println("  ");

  //Serial.print("TOES: "); Serial.print(toes,HEX); Serial.println("  ");
  // //Serial.println(counter);
  // Serial.println("-----------------------------------------------------------");
  Serial.println(gaitService.characteristicCount());

  memcpy(pointer1, &arch,4);        //position 0
  memcpy(pointer1 + 4, &met5,4);    //position 2
  memcpy(pointer1 + 8, &met3,4);    //position 4
  memcpy(pointer1 + 12, &met1,4);    //position 6
  memcpy(pointer2, &heelR,4);   //position 8
  memcpy(pointer2 + 4, &heelL,4);  //position 10
  memcpy(pointer2 + 8, &hallux,4); //position 12
  memcpy(pointer2 + 12, &toes,4);   //position 14
  //memcpy(pointer + 16, &counter,2);//position 16

  // Serial.println (data[0] );
  // Serial.println (data[1]);
  // Serial.println (data[2]);
  // Serial.println (data[3]);
  // Serial.println (data[4]);
  // Serial.println (data[5]);
  // Serial.println (data[6]);
  // Serial.println (data[7]);
  // Serial.println (data[9]);
  // Serial.println (data[10]);
  // Serial.println (data[11]);
  // Serial.println (data[12]);
  // Serial.println (data[13]);
  //Serial.println (data[14],HEX);
  //Serial.println (data[15],HEX);
  // combine the two bytes
  //Serial.println((data[15] << 8) | (data[14]&0x000000FF),HEX);
  //Serial.println((data[15] << 8) | (data[14]&0x000000FF));

  //pointer = data; // reset postion

}
