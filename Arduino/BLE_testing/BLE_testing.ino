#include <ArduinoBLE.h>

//const char* mainServiceUuid = "00000000-0000-1000-8000-00805F9B34FB";
const char* deviceServiceUuid = "00001101-0000-1000-8000-00805F9B34FB";
const char* deviceServiceCharacteristicUuid = "00002101-0000-1000-8000-00805F9B34FB";
uint8_t gesture[2] = {5,10};
BLEService gestureService(deviceServiceUuid); 
BLECharacteristic gestureCharacteristic(deviceServiceCharacteristicUuid, BLERead | BLEWrite|BLENotify,16);


void setup() {

  // Start serial.
  Serial.begin(9600);

  // Ensure serial port is ready.
  while (!Serial);

    if (!BLE.begin()) {
    Serial.println("- Starting Bluetooth® Low Energy module failed!");
    while (1);
  }

  BLE.setLocalName("Arduino Nano 33 BLE (Peripheral)");
  BLE.setAdvertisedService(gestureService);
  gestureService.addCharacteristic(gestureCharacteristic);
  BLE.addService(gestureService);
  //gestureCharacteristic.writeValue(-1);
  BLE.advertise();

  Serial.println("Nano 33 BLE (Peripheral Device)");
  Serial.println(" ");

}

  void loop()
{
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
      gestureCharacteristic.writeValue(gesture,2,false);
      delay(100);
      // if (gestureCharacteristic.written()) {
      //    gesture = gestureCharacteristic.value();
      //    Serial.println(gesture);
      //  }
    }
    
    Serial.println("* Disconnected to central device!");
  }

}
