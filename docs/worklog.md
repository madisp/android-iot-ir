A quick diary about the project
===============================

Project: build a smart (IR) remote that I could use to control all of my
devices at home with my Android smartphone. Requires an WiFi-enabled IR blaster
that will be an Android Things Pi 3.

Anybody reading this: a) you shouldn't b) do note that all of this is from the
perspective of a complete hw noob - this is my first DIY electronics project.

Just notes for myself that I will use when doing a talk and/or a blogpost about
this later.

The first three days are filled in from memory because I wasn't smart enough to
keep a diary at first :)

Day One
-------

Anyhoo, I got the Rpi3 running with the Android Things image. Very simple, just
a simple `dd` to the microsd<sup>[1]</sup>. Once running I took a quick look around the OS.
After booting the Pi I was greeted with a single screen rocking the Things logo
and the `eth0` IP. After connecting ran `adb shellprop` to see what's up. The
device is running API 24 / 7.0. Density is `mdpi`, i.e. `1dp == 1px`. Android
Studio just works, can run normal applications, can debug, can use JRA.

GUI works normally and USB keyboard + mouse work for interaction as well.
Apparently there is no launcher or settings app so configuring wifi requires
firing a service intent with adb<sup>[2]</sup>. Standard doc didn't work because my SSID has
a space in it and `adb shell` treats spaces (even when shellquoted) as separate
args. Doing `adb shell` and then typing in the cmd worked though, yay, WiFi!

The Things library itself is reasonably well documented<sup>[3]</sup> *but* I did not get
javadoc working in the IDE. Which means keeping a browser tab open with the
docs. This was to be expected, this is version `0.1-devpreview` after all :)

First hurdle, I did expect to pretty much connect my IR led directly to a GPIO
pin and be merrily off writing software. My IR LED is rated at 150mA and
apparently you shouldn't try to drive more than 16mA from a GPIO pin<sup>[4]</sup>. OK, so
that means that I need a few resistors and a transistor to drive my LED safely
from GPIO at around 100mA or more.

Much larger hurdle though is the fact that I finally looked at the protocol my
TV-remote uses. It's Philips RC-5<sup>[5]</sup>. It is a ~560 bits/sec manchester-encoding
protocol that sits on top of a pulse-modulated 36khz carrier wave with a 25-33%
duty cycle. That's a lot of buzzwords there, cowboy! Lets break it down to
pieces.

**Manchester encoding.**
As a software guy I would expect the protocol to have some voltage to
mean a `1` and no voltage to mean `0`. In logic circuits it's a bit fuzzy but
similar, a `HIGH` voltage means `1` and a `LOW` voltage means `0` where the
actual levels depend on your chips. For PI `HIGH` is somewhere near 3V and
`LOW` is somewhere near 0V. Fair enough. Manchester encoding however uses
_both_ of these states but in a different order: a `HIGH` followed by a `LOW`
(or a pause really) is `0` and a `LOW` followed by a `HIGH` is `1`. What's
interesting though is that these should be exactly 889 microseconds long.
Which means that we'll be using ~1.8ms per bit when transmitting, where I
got the ~560 bits/sec part.

**Pulse modulation.**
The `HIGH` and `LOW` in the previous paragraph are not actually continuous
signals, instead in the RC-5 protocol they should be a series of short pulses!
The reason behind this is that the IR spectrum is actually quite noisy so our
transmitter would need a lot of power to actually be heard in all that noise.
Imagine a box function where we have a 889 usec-wide box for a transmitted
`HIGH`. Basically the energy required to transmit that signal could be
represented by the area of the box. The height of the box needs to be higher
than the height of the background noise is (should add a drawing here, ascii?).
But there's a neat little trick here. Instead of transmitting continuously lets
transmit a bunch of short bursts that are 4x more powerful than the continous
signal, then pause three times the burst length after each pulse. We're still
using the same amount of energy but our signal is well above the noise level
now. The receiving end needs a band-pass filter to tune in to our frequency
determined by the number and spacing of our pulses and we're good to go.
This is a trick that TV remotes use to save battery life. This is the pulse
modulation carrier in a nutshell. In our case the carrier is at 36khz so this
means that we have 36000 such pulses per second, which is roughly ~27.8usec
per pulse. The duty cycle of 25% means that we need roughly a 7usec pulse
followed by 21usec of pauses, i.e., we're transmitting at 25% of the time.
This is also where the 889 microsecond "bits" in the protocol come from,
they're exactly 32 pulses - `32*27.8 ~ 889`.

There is a pulse modulation facility for Things - `PWM0`. You can configure
both the frequency and the duty cycle on it and then we could use it to send
our signals. But we do need sub-ms precise timing here to enable and disable
the signal and there's no way I could do this from Java or even normal Linux
userland. One helpful person pointed me towards driving both the `PWM` and
`UART` (serial) outputs of the Pi to help me achieve what I wanted but this
would essentially require me to build a modulator in hardware which is out
of my capabilities at this point :). So, alternative approach, lets get an
Arduino board where I can get more precise control over the digital PIN timings
and hook it up to the Pi with a serial interface.

I ended the day by ordering an Arduino Nano and a BC337 NPN-transistor that I
could pick up the next day.

[1]: https://www.raspberrypi.org/documentation/installation/installing-images/mac.md
[2]: https://developer.android.com/things/hardware/raspberrypi.html#connecting_wi-fi
[3]: https://developer.android.com/things/sdk/index.html
[4]: http://raspberrypi.stackexchange.com/questions/9298/what-is-the-maximum-current-the-gpio-pins-can-output
[5]: https://en.wikipedia.org/wiki/RC-5

Day Two
-------

Short day, got the Arduino, hooked it up and ran the blink sample on it. The
internal LED started blinking and I could play with the timings! I.e. if I
swapped it for 10hz then I could see that the blinking went faster. I used
the Arduino IDE which was easy to use but for some reason `ld` fails with exit
code `5` every now and then. Doing a few re-runs fixes the issue, weird.

Day Three
---------

Ok, plan for today is to actually hook up the LED with our transistor and the
Nano and write some software that just turns on my TV. I found a nice webpage
that explains why I need a resistor in the first place and how to calculate the
required resistance<sup>[6]</sup>. I'm going to feed the LED with the 5V I get from Nano.
My led has a forward voltage of 1.3V and it is rated at 150mA. So, to drive it
from 5V I would need a resistance of `(5V-1.3V) / 0.15A = 24.7`. Ohm's law, yay!
Haven't needed to use that one since high school :). I did have a 22ohm one
laying around so I picked that one.

I placed the Arduino on my breadbord and put the LED in series after the
resistor between the 5V pin and ground. You cannot see IR with the naked eye
but you can actually see it with a smartphone camera, which did show that my
LED  was lit after powering on the Nano. Next up, lets make it blink by
putting the BC337 transistor between the LED and ground (LED -> collector,
emitter -> ground) and connecting the base to the D3 output of the Nano.
I modified the blink example to use D3 and verified that my IR led was now
blinking and still lit bright while the Nano did not die in the process.
Awesome!

Next up writing some code. There is an IRlib for Arduino but I'd rather roll my
own because it's fun. I found this example<sup>[7]</sup> that pulses a pin at 38khz 50%
duty cycle and repurposed it to build my own small script that keeps sending
out the standby command in a loop<sup>[8]</sup>. It worked the first time I tried! I had
the unwieldiest TV-remote on the planet - a 24x20cm breadboard with a dangling
USB cord that needed to be connected but I built it myself and it actually
worked!

[6]: http://www.evilmadscientist.com/2012/resistors-for-leds/
[7]: https://github.com/adafruit/Nikon-Intervalometer/blob/master/intervalometer.pde
[8]: https://github.com/madisp/android-iot-ir/blob/6dc1ef75307946fcebac5da46ceee93a268af46e/arduino_ir/arduino_ir.ino

Day Four
--------

My previous circuit had a mistake! I did not put a limiting resistor between
the D3 output pin and the transistor base. I think it worked still ok because I
had the transistor but just in case I put a 10k resistor between the D3 output
pin and the base of the transistor.

Time to write some simple code so that I could control the Nano from the Pi.
I'm thinking of a real simple serial protocol where each byte that is sent is
just the direct RC-5 command. Address will be hardcoded at 0.

Also by looking at the pinouts of the Pi and the Nano I noticed that the GPIO
pins on the Pi operate on 3.3V whereas the ones on my arduino board operate on
5V. Turns out that I can get around this by getting a small logic level
converter board that I can use to hook up the Pi to Nano. I ordered it and
a new Arduino board as well - the Adafruit Trinket<sup>[9]</sup> because it is small and
operates on 3.3V too so I could connect it directly to Pi.

Finally figured I should start keeping a diary so that I could actually
remember all of this later.

[9]: https://www.adafruit.com/product/1500

Day Five
--------

While waiting for the Trinket I started thinking about doing everything with
the Pi using two NPN-transistors, the PWM0 and the UART port to modulate a
signal without the need of precise timings in the userland. I didn't come up
with this idea - thanks Dave. Also, thanks Taavi for being my rubber duck and
letting me know that I'm not delusional in thinking that I could use
transistors this way.

The idea itself is quite simple and builds on what I've learned to connect the
LED to the Arduino. I start with the 36khz 25% signal which I can easily get
from PWM0. Then I feed that to a transistor base which collects from the +3.3V
Pi output. Now I take that signal and send it to the next transistor collector
where the base is connected to the UART TX pin, where I can essentially use the
UART drivers to enable/disable the modulater 36khz signal. Since each RC-5
command is 14 bits long (28 manchester-encoded half bits) I can abuse UART by
just configuring it to 7 bits, 1125, no start or stop bits. This will give me
the 889usec pulses 36khz modulated pulses I've been looking for.

I also found this awesome website that has a Java applet that can animate
what is up with your circuit. It even has a small oscilloscope function that
can display the generated waveforms. I tested out my idea there and it looks
like it should work<sup>[10]</sup>.

[10]: http://tinyurl.com/jxnhfb2

Day Six
-------

The trinket and logic level switcher have arrived but I'm going to try to do
it without the Arduino first, as I planned yesterday. Not worried at all
about stuff being left lying around as this is super interesting! I'm already
thinking about what my next project is going to be :D

OK, UART is not going to work! Apparently the hardware does not support setting
no stop bits which I would require to send my signal correctly. However, maybe
I can achieve the same thing using SPI.

Few hours later - I couldn't get it to work. Still don't know why, checked that
the circuit was OK but it just didn't work. Luckily I had both the Nano+LLC or
the trinket. The latter needed soldering headers which I hadn't done for a
while.
