package pink.madis.things.ir.devices.yamaha

import okhttp3.OkHttpClient
import pink.madis.things.ir.AvReceiver
import pink.madis.things.ir.Device
import pink.madis.things.ir.State

/**
 * A class that can talk to my Yamaha RXV-681
 */
internal const val MAIN_ZONE = "Main_Zone"

class YamahaRxv(val ip: String): AvReceiver, Device {

    override var input: String
        // req:
        //   POST http://${ip}/YamahaRemoteControl/ctrl
        // <YAMAHA_AV cmd="GET"><Main_Zone><Basic_Status>GetParam</Basic_Status></Main_Zone></YAMAHA_AV>
        get() = TODO("not implemented")
        set(value) {
            // req:
            //   POST http://${ip}/YamahaRemoteControl/ctrl Content-Type:
            // body:
            // <YAMAHA_AV cmd="PUT"><Main_Zone><Input><Input_Sel>HDMI1</Input_Sel></Input></Main_Zone></YAMAHA_AV>
        }
    override var state: State
        get() = TODO("not implemented")
        set(value) {}
}