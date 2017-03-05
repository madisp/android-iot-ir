package pink.madis.things.ir

class Home(val avReceiver: AvReceiver, val devices: List<Device>) {
    fun standby() {
        devices.forEach { it.state = State.STANDBY }
    }

    fun switchTo(p: Preset) {
        devices.forEach {
            if (p.devices.contains(it)) {
                it.state = State.POWERED
            } else {
                it.state = State.STANDBY
            }
        }
        avReceiver.input = p.avInput
    }
}