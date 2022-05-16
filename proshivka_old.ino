#include <WiegandMega2560.h>

#define FALSE 0
#define TRUE  1


WIEGAND wg;
String serialData = "";
void setup() {
  // put your setup code here, to run once:
  /* 
    f2 nz pin mode 44
    f1 nz pin mode 41
  */
  Serial.begin(9600);
	wg.begin(TRUE, TRUE, FALSE);  // wg.begin(GateA , GateB, GateC)

  // -- Считыватели, лампочки, реле
  pinMode(4, OUTPUT); // exit success
  pinMode(5, OUTPUT); // exit error
  pinMode(6, OUTPUT); // enter error
  pinMode(7, OUTPUT); // enter success
  pinMode(8, OUTPUT);
  pinMode(9, OUTPUT); 

  digitalWrite(4, HIGH);
  digitalWrite(5, HIGH);
  digitalWrite(6, HIGH);
  digitalWrite(7, HIGH);
  digitalWrite(8, HIGH);
  digitalWrite(9, HIGH);
  // --

  // -- Для прокрутки турникета
  pinMode(50, INPUT); // F2 NZ
  pinMode(48, INPUT); // F1 NZ
  pinMode(51, OUTPUT); // F2 comman
  pinMode(49, OUTPUT); // F1 comman
  digitalWrite(50, HIGH);
  digitalWrite(48, HIGH);
  // --
  
}

String getValue(String data, char separator, int index)
{
    int found = 0;
    int strIndex[] = { 0, -1 };
    int maxIndex = data.length() - 1;

    for (int i = 0; i <= maxIndex && found <= index; i++) {
        if (data.charAt(i) == separator || i == maxIndex) {
            found++;
            strIndex[0] = strIndex[1] + 1;
            strIndex[1] = (i == maxIndex) ? i+1 : i;
        }
    }
    return found > index ? data.substring(strIndex[0], strIndex[1]) : "";
}


void loop() {
  delay(308);

  // Проверка прохода
  if (digitalRead(48) == HIGH) {
    delay(200);
    digitalWrite(7, HIGH);
    digitalWrite(9, HIGH);
    Serial.println("enter-success");
  }
  if (digitalRead(50) == HIGH) {
    delay(200);
    digitalWrite(4, HIGH);
    digitalWrite(8, HIGH);
    Serial.println("exit-success");
  } 

  // put your main code here, to run repeatedly:
  if(wg.available()) {
    Serial.print("@Code=");
    Serial.print(wg.getCode());
    Serial.print(";");
    if (wg.getGateActive() == 1) {
      Serial.print("@Direction=enter"); 
    } else if (wg.getGateActive() == 2) {
      Serial.print("@Direction=exit"); 
    }
    Serial.println("");
	}

  if(Serial.available() > 0) {
    serialData = Serial.readString();
    serialData.trim();
    String code = getValue(serialData, ';', 0);
    String reader = getValue(serialData, ';', 1);
    code = getValue(code, '=', 1);
    reader = getValue(reader, '=', 1);

    // @Code=user-not-found;@reader=exit or @Code=user-not-found;@reader=enter
    if (code == "user-not-found") {
      if (reader == "exit") {
        digitalWrite(5, LOW);
        delay(1500);
        digitalWrite(5, HIGH);
      }
      else if (reader == "enter") {
        digitalWrite(6, LOW);
        delay(1500);
        digitalWrite(6, HIGH);
      }
    }

    // @Code=user-success;@reader=exit or @Code=user-success;@reader=enter
    if (code == "user-success") {
      if (reader == "exit") {
        digitalWrite(4, LOW);
        digitalWrite(8, LOW);
      }
      else if (reader == "enter") {
        digitalWrite(7, LOW);
        digitalWrite(9, LOW);
      }
    }

    // 
    // @Code=lock;@reader=exit or @Code=user-success;@reader=enter
    if (code == "lock") {
      if (reader == "exit") {
        digitalWrite(4, HIGH);
        digitalWrite(8, HIGH);
      }
      else if (reader == "enter") {
        digitalWrite(7, HIGH);
        digitalWrite(9, HIGH);
      }
    }
  }
}
