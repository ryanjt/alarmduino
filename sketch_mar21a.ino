

/*
  LiquidCrystal Library - Hello World

 Demonstrates the use a 16x2 LCD display.  The LiquidCrystal
 library works with all LCD displays that are compatible with the
 Hitachi HD44780 driver. There are many of them out there, and you
 can usually tell them by the 16-pin interface.

 This sketch prints "Hello World!" to the LCD
 and shows the time.

  The circuit:
 * LCD RS pin to digital pin 12
 * LCD Enable pin to digital pin 11
 * LCD D4 pin to digital pin 5
 * LCD D5 pin to digital pin 4
 * LCD D6 pin to digital pin 3
 * LCD D7 pin to digital pin 2
 * LCD R/W pin to ground
 * LCD VSS pin to ground
 * LCD VCC pin to 5V
 * 10K resistor:
 * ends to +5V and ground
 * wiper to LCD VO pin (pin 3)

 Library originally added 18 Apr 2008
 by David A. Mellis
 library modified 5 Jul 2009
 by Limor Fried (http://www.ladyada.net)
 example added 9 Jul 2009
 by Tom Igoe
 modified 22 Nov 2010
 by Tom Igoe

 This example code is in the public domain.

 http://www.arduino.cc/en/Tutorial/LiquidCrystal
 */

// include the library code:
#include <LiquidCrystal.h>
#include <virtuabotixRTC.h>
#include <SoftwareSerial.h>// import the serial library
int piezoPin = 6;
char c = ' ';
String alarmHour = "21";
String alarmMinute = "";
bool alarmOn = true; 
SoftwareSerial BTserial(13, 12); // RX | TX
// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(8, 7, 5, 4, 3, 2);
virtuabotixRTC myRTC(11, 10,9);
void setup() {
  Serial.begin(38400);
    Serial.println("Arduino is ready");
    Serial.println("Remember to select Both NL & CR in the serial monitor");
  
    // HC-05 default serial speed for AT mode is 38400
    BTserial.begin(9600);  
   //Serial.begin(9600);
   //myRTC.setDS1302Time(0, 06, 21, 4, 21, 3, 2019);
  // set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  // Print a message to the LCD.
  lcd.setCursor(0, 1);
  lcd.print("Alarm : " + alarmHour + ":" + alarmMinute);
}
void alarm(){
  tone(piezoPin, 3000, 500);
  delay(1000);
}
void loop() {
  
  // set the cursor to column 0, line 1
  // (note: line 1 is the second row, since counting begins with 0):
  lcd.setCursor(0, 0);
  // print the number of seconds since reset:
  lcd.print(String(myRTC.hours) + ":" + String(myRTC.minutes) + ":" + String(myRTC.seconds));
   myRTC.updateTime();
//if (String(myRTC.hours) == alarmHour & String(myRTC.minutes) == alarmMinute & String(myRTC.seconds) == "0" & alarmOn == true){
//  alarm();
//  
//}
//// Start printing elements as individuals
// Serial.print("Current Date / Time: ");
// Serial.print(myRTC.dayofmonth); //You can switch between day and month if you're using American system
// Serial.print("/");
// Serial.print(myRTC.month);
// Serial.print("/");
// Serial.print(myRTC.year);
// Serial.print(" ");
// Serial.print(myRTC.hours);
// Serial.print(":");
// Serial.print(myRTC.minutes);
// Serial.print(":");
// Serial.println(myRTC.seconds);

// Delay so the program doesn't print non-stop
// delay(1000);
if (BTserial.available())
    {  
        c = BTserial.read();
        Serial.write(c);
        delay(3);
        lcd.setCursor(0, 1);
  lcd.print("Alarm : " + alarmHour + ":" + c);
    lcd.setCursor(0, 0);
  lcd.print(String(myRTC.hours) + ":" + String(myRTC.minutes) + ":" + String(myRTC.seconds));
if (String(myRTC.hours) == alarmHour & String(myRTC.minutes) == c & String(myRTC.seconds) == "0" & alarmOn == true){
  alarm();
  
}
    }
  
    // Keep reading from Arduino Serial Monitor and send to HC-05
    if (Serial.available())
    {
        c =  Serial.read();
        BTserial.write(c);  
    }
}
