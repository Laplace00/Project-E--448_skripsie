#include <Adafruit_ADS1X15.h>
#include <ArduinoBLE.h>

Adafruit_ADS1115 ads;  /* Use this for the 16-bit version */

const char* deviceServiceUuid = "00001101-0000-1000-8000-00805F9B34FB";
const char* deviceServiceCharacteristicUuid = "00002101-0000-1000-8000-00805F9B34FB";

void setup(void)
{
  Serial.begin(9600);
  pinMode(A0,INPUT_PULLUP);
  pinMode(A1,INPUT_PULLUP);
  pinMode(A2,INPUT_PULLUP);
  pinMode(A3,INPUT_PULLUP);
  pinMode(A6,INPUT_PULLUP);
  pinMode(A7,INPUT_PULLUP);
  Serial.println("Hello!");

  Serial.println("Getting single-ended readings from AIN0..3");
  Serial.println("ADC Range: +/- 6.144V (1 bit = 3mV/ADS1015, 0.1875mV/ADS1115)");

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

  if (!ads.begin()) {
    Serial.println("Failed to initialize ADS.");
    while (1);
  }
}

void loop(void)
{
  analogReadResolution(12);
  int16_t adc0,adc3;
  int adcA0,adcA1,adcA2,adcA3,adcA6,adcA7;
  float volts0, volts1, volts2, volts3;

  adc0 = ads.readADC_SingleEnded(0);
  //adc1 = ads.readADC_SingleEnded(1);
  //adc2 = ads.readADC_SingleEnded(2);
  adc3 = ads.readADC_SingleEnded(3);
  adcA0 = analogRead(A0);
  adcA1 = analogRead(A1);
  adcA2 = analogRead(A2);
  adcA3 = analogRead(A3);
  adcA6 = analogRead(A6);
  adcA7 = analogRead(A7);
  

  volts0 = ads.computeVolts(adc0);
  //volts1 = ads.computeVolts(adc1);
  //volts2 = ads.computeVolts(adc2);
  volts3 = ads.computeVolts(adc3);

  adc0 = map(adc0,0,17200,0,4096);
  adc3 = map(adc3,0,17200,0,4096);
  Serial.println("-----------------------------------------------------------");
  Serial.print("ARCH: "); Serial.print(adc0); Serial.print("  "); Serial.print(volts0); Serial.println("V");
 
  Serial.print("MET5: "); Serial.print(adc3); Serial.print("  "); Serial.print(volts3); Serial.println("V");
  Serial.print("MET3: "); Serial.print(adcA0); Serial.println("  ");
  Serial.print("MET1: "); Serial.print(adcA1); Serial.println("  ");
  Serial.print("HEEL_R: "); Serial.print(adcA2); Serial.println("  ");
  Serial.print("HEEL_L: "); Serial.print(adcA3); Serial.println("  ");
  Serial.print("HALLUX: "); Serial.print(adcA6); Serial.println("  ");
  Serial.print("TOES: "); Serial.print(adcA7); Serial.println("  ");
  delay(1000);
}
