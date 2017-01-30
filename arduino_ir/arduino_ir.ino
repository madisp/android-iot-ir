#include <Wire.h>

const int IRLED = 2;
const int PTT = 3;
unsigned int toggle = 0;

void setup() {
  pinMode(IRLED, OUTPUT);
  pinMode(PTT, INPUT);
  Wire.begin(8);
  Wire.onReceive(wireEvent);
  Serial.begin(9600);
  Serial.println("Transmitter ready and waiting");
}

void wireEvent(int n) {
  Serial.print("Received ");
  Serial.print(n);
  Serial.println(" bytes over Wire");
  
  while (Wire.available() < 2);
  int addr = Wire.read();
  int cmd = Wire.read();
  Serial.print("Cmd received addr: ");
  Serial.print(addr, HEX);
  Serial.print(" cmd: ");
  Serial.println(cmd, HEX);
  command(addr, cmd);
}

void loop() {
  delay(100);
}

void command(int addr, int cmd) {
  if (toggle == 1) {
    toggle = 0;
  } else {
    toggle = 1;
  }

  // start bits
  tx(1); tx(1);
  // toggle bit 0
  tx(toggle);
  // 5 bits for addr
  for (int i = 4; i >= 0; --i) {
    tx(addr & (1<<i));
  }
  // 6 bits for the cmd
  for (int i = 5; i >= 0; --i) {
    tx(cmd & (1<<i));
  }
}

void tx(int bit) {
  if (bit != 0) {
    delayMicroseconds(884); // leave 5 usec for the call itself
    pulseIR(889);
  } else {
    pulseIR(889);
    delayMicroseconds(884); // leave 5 usec for the call itself
  }
}

void pulseIR(long usec) {
  // we'll count down from the number of microseconds we are told to wait
  cli();  // this turns off any background interrupts
  while (usec > 0) {
   // 36kHz, duty 25% is ~28usec, 7us high, 21us low
   // assume digital write takes 3usec, that leaves us with delays of 7-3 and 21-3, 4 and 18
   digitalWrite(IRLED, HIGH);
   delayMicroseconds(4);
   digitalWrite(IRLED, LOW);
   delayMicroseconds(18);
   usec -= 28;
  }
  sei();  // this turns them back on
}
