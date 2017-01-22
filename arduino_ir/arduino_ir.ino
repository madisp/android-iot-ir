
const int IRLED = 3;
unsigned int toggle = 0;

void setup() {
  Serial.begin(9600);
  pinMode(IRLED, OUTPUT);
  pinMode(LED_BUILTIN, OUTPUT);
}

void loop() {
  digitalWrite(LED_BUILTIN, HIGH);
  command(0x0c);
  // show the led a bit longer
  delay(1000);
  digitalWrite(LED_BUILTIN, LOW);
  
  // sleep a bit and try again
  delay(5000);
}

void command(int cmd) {
  if (toggle == 1) {
    toggle = 0;
  } else {
    toggle = 1;
  }

  // start bits
  tx(1); tx(1);
  // toggle bit 0
  tx(toggle);
  // address 0x0
  tx(0); tx(0); tx(0); tx(0); tx(0);
  // 6 bits for the cmd
  for (int i = 0; i < 6; ++i) {
    tx(cmd & 1);
    cmd >>= 1;
  }
}

void tx(int bit) {
  if (bit == 1) {
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
