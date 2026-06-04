#include <Wire.h>
#include <Adafruit_GFX.h>
#include <Adafruit_SSD1306.h>
#include <SoftwareSerial.h>

#define SCREEN_WIDTH 128
#define SCREEN_HEIGHT 64

Adafruit_SSD1306 display(
128,
64,
&Wire,
-1
);

SoftwareSerial BT(10,11);

String incomingData = "";

String currentTime = "00:00";

bool callScreen = false;

bool popupScreen = false;

unsigned long popupTimer = 0;

// CLOCK SCREEN
void drawClock() {

  display.clearDisplay();

  display.drawRect(18,14,92,34,WHITE);

  display.setTextSize(2);

  display.setTextColor(WHITE);

  display.setCursor(24,24);

  display.println(currentTime);

  display.display();
}

// NOTIFICATION SCREEN
void drawNotification(String msg) {

  display.clearDisplay();

  display.drawRect(2,2,124,60,WHITE);

  display.setTextSize(1);

  display.setTextColor(WHITE);

  display.setCursor(8,20);

  display.println(msg);

  display.display();
}

// CALL SCREEN
void drawCall(String name) {

  display.clearDisplay();

  display.drawRect(8,8,112,48,WHITE);

  // CALL TITLE
  display.setTextSize(1);

  display.setTextColor(WHITE);

  display.setCursor(46,14);

  display.println("CALL");

  // CALLER NAME
  display.setTextSize(1);

  int x =
      (128 - (name.length() * 6)) / 2;

  if(x < 10) x = 10;

  display.setCursor(x,32);

  display.println(name);

  display.display();
}

void setup() {

  Serial.begin(9600);

  BT.begin(9600);

  // OLED START
  if(!display.begin(
      SSD1306_SWITCHCAPVCC,
      0x3C)) {

    while(1);
  }

  display.clearDisplay();

  display.setTextSize(2);

  display.setTextColor(WHITE);

  display.setCursor(18,24);

  display.println("VISION");

  display.display();

  delay(2000);

  drawClock();
}

void loop() {

  while(BT.available()) {

    char c = BT.read();

    if(c == '\n') {

      Serial.println(incomingData);

      // TIME
      if(incomingData.startsWith("TIME")) {

        currentTime =
            incomingData.substring(5);

        if(!callScreen &&
           !popupScreen) {

          drawClock();
        }
      }

      // CALL
      else if(incomingData.startsWith("CALL")) {

        String caller =
            incomingData.substring(5);

        callScreen = true;

        drawCall(caller);
      }

      // CALL END
      else if(incomingData.startsWith("ENDCALL")) {

        callScreen = false;

        drawClock();
      }

      // NOTIFICATION
      else {

        popupScreen = true;

        popupTimer = millis();

        drawNotification(incomingData);
      }

      incomingData = "";
    }

    else {

      incomingData += c;
    }
  }

  // REMOVE NOTIFICATION
  if(popupScreen &&
     millis() - popupTimer > 5000) {

    popupScreen = false;

    if(!callScreen) {

      drawClock();
    }
  }
}
