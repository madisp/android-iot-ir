package pink.madis.things.ir

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HomeTest {
    @Test
    fun singleDeviceTurnedOn() {
        val device = FakeDevice(State.STANDBY)
        val receiver = FakeReceiver("initial")
        val preset = Preset("input1", listOf(device))
        val home = Home(receiver, listOf(device))
        home.switchTo(preset)

        assertThat(receiver.input).isEqualTo("input1")
        assertThat(device.state).isEqualTo(State.POWERED)
    }

    @Test
    fun otherDevicesGetTurnedOff() {
        val device = FakeDevice(State.POWERED)
        val device2 = FakeDevice(State.POWERED)
        val device3 = FakeDevice(State.POWERED)
        val receiver = FakeReceiver("initial")
        val preset = Preset("input1", listOf(device2))
        val home = Home(receiver, listOf(device, device2, device3))
        home.switchTo(preset)

        assertThat(receiver.input).isEqualTo("input1")
        assertThat(device.state).isEqualTo(State.STANDBY)
        assertThat(device2.state).isEqualTo(State.POWERED)
        assertThat(device3.state).isEqualTo(State.STANDBY)
    }

    @Test
    fun standbyTurnsEverythingOff() {
        val receiver = FakeReceiver("initial")
        val home = Home(receiver, listOf(FakeDevice(State.POWERED)))
        home.standby()
        assertThat(receiver.input).isEqualTo("initial")
        assertThat(home.devices.all { it.state == State.STANDBY }).isTrue()
    }
}

internal class FakeReceiver(override var input: String): AvReceiver
internal class FakeDevice(override var state: State): Device