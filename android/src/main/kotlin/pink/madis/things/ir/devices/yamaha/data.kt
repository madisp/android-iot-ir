package pink.madis.things.ir.devices.yamaha

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(strict = false)
data class StatusReply(
        @field:Element(required = false) var Main_Zone: Zone?,
        @field:Element(required = false) var Zone_2: Zone?,
        @field:Attribute var RC: String,
        @field:Attribute var rsp: String
        ) {
    constructor(): this(null, null, "", "")
}

data class Zone(
        @field:Element var Basic_Status: BasicStatus
) {
    constructor(): this(BasicStatus())
}

@Root(strict = false)
data class BasicStatus(
        @field:Element var Power_Control: PowerControl,
        @field:Element var Volume: Volume,
        @field:Element var Input: Input,
        @field:Element var Party_Info: String,
        @field:Element(required=false) var Sound_Video: SoundVideo?
) {
    constructor(): this(PowerControl(), Volume(), Input(), "", null)
}

data class PowerControl(
        @field:Element var Power: String,
        @field:Element var Sleep: Boolean
) {
    constructor(): this("", false)
}

@Root(strict = false)
data class Volume(
        @field:Element var Lvl: VolumeLevel,
        @field:Element var Mute: Boolean
) {
    constructor(): this(VolumeLevel(), false)
}

data class VolumeLevel(
        @field:Element var Val: Int,
        @field:Element var Exp: Int,
        @field:Element var Unit: String
) {
    constructor(): this(0, 0 ,"")
}

@Root(strict = false)
data class Input(
        @field:Element var Input_Sel: String
) {
    constructor(): this("")
}

@Root(strict = false)
data class SoundVideo(
        @field:Element var Tone: Tone,
        @field:Element var Pure_Direct: PureDirect,
        @field:Element var Extra_Bass: Boolean
) {
    constructor(): this(Tone(), PureDirect(), false)
}

data class Tone(
        @field:Element var Bass: VolumeLevel,
        @field:Element var Treble: VolumeLevel
) {
    constructor(): this(VolumeLevel(), VolumeLevel())
}

data class PureDirect(
        @field:Element var Mode: Boolean
) {
    constructor(): this(false)
}