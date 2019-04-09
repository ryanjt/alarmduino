
// include the library code:
#include <LiquidCrystal.h>
#include <virtuabotixRTC.h>
#include <SoftwareSerial.h>// import the serial library
int piezoPin = 6;
String c = "";
String content = "";
String alarmHour = "";
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

if (BTserial.available())
    {  
      while(BTserial.available()){
        c = BTserial.readStringUntil(':');
        content.concat(c);
        alarmHour = c;
        Serial.println(alarmHour);
        content = "";
        Serial.read(); 
        c = BTserial.readStringUntil(':');
        content.concat(c);
        alarmMinute =  c;
      }
      if (content != ""){
        Serial.println(content); 
          lcd.setCursor(0, 1);
  lcd.print("Alarm : " + alarmHour + ":" + alarmMinute);
    lcd.setCursor(0, 0);
  lcd.print(String(myRTC.hours) + ":" + String(myRTC.minutes) + ":" + String(myRTC.seconds));
      }
        //c = BTserial.read();
        //Serial.write(c);
        //delay(3);
      
if (String(myRTC.hours) == String(alarmHour) & String(myRTC.minutes) ==  String(alarmMinute) & alarmOn == true){
  alarm();
  
}
    }
  
    // Keep reading from Arduino Serial Monitor and send to HC-05
    if (Serial.available())
    {
        c =  Serial.read();
        BTserial.write("1");  
        Serial.println(alarmHour);
        Serial.println(alarmMinute);
    }
}