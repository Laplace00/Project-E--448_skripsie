#include <Adafruit_ADS1X15.h>
#include <ArduinoBLE.h>

Adafruit_ADS1115 ads;  // Use this for ADS1115 
const char* deviceServiceUuid = "00001101-0000-1000-8000-00805F9B34FB";
const char* deviceServiceCharacteristicUuid1 = "00002101-0000-1000-8000-00805F9B34FB";
const char* deviceServiceCharacteristicUuid2 = "00003101-0000-1000-8000-00805F9B34FB";
int8_t data1[16] = {0};
int8_t data2[16] = {0};
int8_t* pointer1 = data1;
int8_t* pointer2 = data2;
float arch,met5,met3,met1,heelR,heelL,hallux,toes,counter,arch_init,met5_init,arch_max,met5_max;

BLEService gaitService(deviceServiceUuid); 
BLECharacteristic gaitCharacteristic1(deviceServiceCharacteristicUuid1, BLERead | BLEWrite|BLENotify,16);
BLECharacteristic gaitCharacteristic2(deviceServiceCharacteristicUuid2, BLERead | BLEWrite|BLENotify,16);
void getReadings();

void setup(void)
{
  Serial.begin(9600);


  // pinMode setup each pin with internal pullup resistors
  pinMode(A0,INPUT_PULLUP);
  pinMode(A1,INPUT_PULLUP);
  pinMode(A2,INPUT_PULLUP);
  pinMode(A3,INPUT_PULLUP);
  pinMode(A6,INPUT_PULLUP);
  pinMode(A7,INPUT_PULLUP);

  // Config ADC Resolution
  analogReadResolution(12);

  // check for error
  if (!ads.begin()) {
    Serial.println("Failed to initialize ADS1115.");
    while (1);
  }

  // BLE setup
    if (!BLE.begin()) {
    Serial.println("BLE error-Ble could not start");
    while (1);
  }
  BLE.setLocalName("Arduino Nano 33 BLE (Peripheral)");
  BLE.setAdvertisedService(gaitService);
  gaitService.addCharacteristic(gaitCharacteristic1);
  gaitService.addCharacteristic(gaitCharacteristic2);
  BLE.addService(gaitService);
  BLE.advertise();
 

  


}

void loop(void)
{

  //volts0 = ads.computeVolts(arch);
  //volts1 = ads.computeVolts(adc1);
  //volts2 = ads.computeVolts(adc2);
  //volts3 = ads.computeVolts(met5);

 
  // Prints Arduino MAC address while waiting for connection
  Serial.print("MAC: ");
  Serial.println(BLE.address());
  BLEDevice central = BLE.central();
  Serial.println("- Discovering central device...");

  // If Device is connected we enter if loop
  if (central) {

    // Prints central device MAC address
    Serial.println("* Connected to central device!");
    Serial.print("* Device MAC address: ");
    Serial.println(central.address());
    Serial.println(" ");

    // Stay in this loop while connected to the Android device
    while (central.connected()) {   
      getReadings(); // get ADC readings
      gaitCharacteristic1.writeValue(&data1,16,false); // writes to first Characteristic
      gaitCharacteristic2.writeValue(&data2,16,false); // writes to second Characteristic
      delay(50);
      }
    
    Serial.println("* Disconnected to central device!");
  }

}

void getReadings(){
  // Get ADC readings from ADS1115
  arch_init = ads.readADC_SingleEnded(0);
  arch_max = max(arch_max,arch_init);
  met5_init = ads.readADC_SingleEnded(3);
  met5_max = max(met5_max,met5_init);
  arch = map(arch_init,0,arch_max,0,4096);
  met5 = map(met5_init,0,met5_max,0,4096);

  // Get ADC from onboard pins
  met3 = analogRead(A0);
  met1 = analogRead(A1);
  heelR = analogRead(A2);
  heelL = analogRead(A3);
  hallux = analogRead(A6);
  toes = analogRead(A7);



  Serial.println("-----------------------------------------------------------");
  Serial.print("ARCH: "); Serial.print(arch); Serial.print("  "); //Serial.print(volts0); Serial.println("V");
  Serial.print("MET3: "); Serial.print(met3); Serial.println("  ");
  Serial.print("MET5: "); Serial.print(met5); Serial.print("  "); //Serial.print(volts3); Serial.println("V");
  Serial.print("MET1: "); Serial.print(met1); Serial.println("  ");
  Serial.print("HEEL_R: "); Serial.print(heelR); Serial.println("  ");
  Serial.print("HEEL_L: "); Serial.print(heelL); Serial.println("  ");
  Serial.print("HALLUX: "); Serial.print(hallux); Serial.println("  ");
  Serial.print("TOES: "); Serial.print(toes,HEX); Serial.println("  ");
  Serial.println("-----------------------------------------------------------");

  //Store first 4 readings in data1[]
  memcpy(pointer1, &arch,4);        //position 0
  memcpy(pointer1 + 4, &met5,4);    //position 2
  memcpy(pointer1 + 8, &met3,4);    //position 4
  memcpy(pointer1 + 12, &met1,4);    //position 6
  // Store the other 4 readings in data2[]
  memcpy(pointer2, &heelR,4);   //position 8
  memcpy(pointer2 + 4, &heelL,4);  //position 10
  memcpy(pointer2 + 8, &hallux,4); //position 12
  memcpy(pointer2 + 12, &toes,4);   //position 14
}
