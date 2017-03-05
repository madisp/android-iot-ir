package pink.madis.things.ir.devices

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.simpleframework.xml.core.Persister
import org.simpleframework.xml.stream.Format
import pink.madis.things.ir.devices.yamaha.*
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets

const val mainZoneBasicStatus: String = """
<YAMAHA_AV RC="0" rsp="GET">
	<Main_Zone>
		<Basic_Status>
			<Power_Control>
				<Power>Standby</Power>
				<Sleep>Off</Sleep>
			</Power_Control>
			<Volume>
				<Lvl>
					<Val>-430</Val>
					<Exp>1</Exp>
					<Unit>dB</Unit>
				</Lvl>
				<Mute>Off</Mute>
				<Subwoofer_Trim>
					<Val>0</Val>
					<Exp>1</Exp>
					<Unit>dB</Unit>
				</Subwoofer_Trim>
			</Volume>
			<Input>
				<Input_Sel>Spotify</Input_Sel>
				<Input_Sel_Item_Info>
					<Param>Spotify</Param>
					<RW>RW</RW>
					<Title>Spotify</Title>
					<Icon>
						<On>/YamahaRemoteControl/Icons/icon102.png</On>
						<Off/>
					</Icon>
					<Src_Name>Spotify</Src_Name>
					<Src_Number>1</Src_Number>
				</Input_Sel_Item_Info>
			</Input>
			<Surround>
				<Program_Sel>
					<Current>
						<Straight>On</Straight>
						<Enhancer>Off</Enhancer>
						<Sound_Program>7ch Stereo</Sound_Program>
					</Current>
				</Program_Sel>
				<_3D_Cinema_DSP>Off</_3D_Cinema_DSP>
			</Surround>
			<Party_Info>Off</Party_Info>
			<Sound_Video>
				<Tone>
					<Bass>
						<Val>0</Val>
						<Exp>1</Exp>
						<Unit>dB</Unit>
					</Bass>
					<Treble>
						<Val>0</Val>
						<Exp>1</Exp>
						<Unit>dB</Unit>
					</Treble>
				</Tone>
				<Pure_Direct>
					<Mode>Off</Mode>
				</Pure_Direct>
				<HDMI>
					<Standby_Through_Info>On</Standby_Through_Info>
					<Output>
						<OUT_1>On</OUT_1>
					</Output>
				</HDMI>
				<YPAO_Volume>Off</YPAO_Volume>
				<Extra_Bass>Off</Extra_Bass>
				<Adaptive_DRC>Off</Adaptive_DRC>
				<Dialogue_Adjust>
					<Dialogue_Lift>0</Dialogue_Lift>
					<Dialogue_Lvl>0</Dialogue_Lvl>
					<DTS_Dialogue_Control>0</DTS_Dialogue_Control>
				</Dialogue_Adjust>
			</Sound_Video>
		</Basic_Status>
	</Main_Zone>
</YAMAHA_AV>
"""

const val zone2basicStatus: String = """
<YAMAHA_AV RC="0" rsp="GET">
	<Zone_2>
		<Basic_Status>
			<Power_Control>
				<Power>Standby</Power>
				<Sleep>Off</Sleep>
			</Power_Control>
			<Volume>
				<Lvl>
					<Val>-400</Val>
					<Exp>1</Exp>
					<Unit>dB</Unit>
				</Lvl>
				<Mute>Off</Mute>
				<Output_Info>Variable</Output_Info>
			</Volume>
			<Input>
				<Input_Sel>AUDIO1</Input_Sel>
				<Input_Sel_Item_Info>
					<Param>AUDIO1</Param>
					<RW>RW</RW>
					<Title>AUDIO1</Title>
					<Icon>
						<On>/YamahaRemoteControl/Icons/icon002.png</On>
						<Off/>
					</Icon>
					<Src_Name/>
					<Src_Number>1</Src_Number>
				</Input_Sel_Item_Info>
			</Input>
			<Party_Info>Off</Party_Info>
		</Basic_Status>
	</Zone_2>
</YAMAHA_AV>
"""

class YamahaRxvTest {
    @Test
    fun testReadMainZone() {
        val xml = Persister()
        val status = xml.read(StatusReply::class.java, mainZoneBasicStatus)
        assertThat(status.Main_Zone).isNotNull()
        assertThat(status.Zone_2).isNull()
        assertThat(status.RC).isEqualTo("0")
        assertThat(status.rsp).isEqualTo("GET")

        status.Main_Zone?.let {
            assertThat(it.Basic_Status.Input.Input_Sel).isEqualTo("Spotify")
            assertThat(it.Basic_Status.Power_Control.Power).isEqualTo("Standby")
            assertThat(it.Basic_Status.Power_Control.Sleep).isEqualTo(false)
            assertThat(it.Basic_Status.Party_Info).isEqualTo("Off") // boolean?
            assertThat(it.Basic_Status.Volume.Lvl.Val).isEqualTo(-430)
            assertThat(it.Basic_Status.Volume.Lvl.Exp).isEqualTo(1)
            assertThat(it.Basic_Status.Volume.Lvl.Unit).isEqualTo("dB")
            assertThat(it.Basic_Status.Volume.Mute).isEqualTo(false)
            assertThat(it.Basic_Status.Sound_Video).isNotNull()
            it.Basic_Status.Sound_Video?.let {
                assertThat(it.Extra_Bass).isEqualTo(false)
                assertThat(it.Pure_Direct.Mode).isEqualTo(false)
                assertThat(it.Tone.Bass.Val).isEqualTo(0)
                assertThat(it.Tone.Bass.Exp).isEqualTo(1)
                assertThat(it.Tone.Bass.Unit).isEqualTo("dB")
                assertThat(it.Tone.Treble.Val).isEqualTo(0)
                assertThat(it.Tone.Treble.Exp).isEqualTo(1)
                assertThat(it.Tone.Treble.Unit).isEqualTo("dB")
            }
        }
    }

    @Test
    fun testReadZone2() {
        val xml = Persister()
        val status = xml.read(StatusReply::class.java, zone2basicStatus)
        assertThat(status.Main_Zone).isNull()
        assertThat(status.Zone_2).isNotNull()
        assertThat(status.RC).isEqualTo("0")
        assertThat(status.rsp).isEqualTo("GET")

        status.Zone_2?.let {
            assertThat(it.Basic_Status.Input.Input_Sel).isEqualTo("AUDIO1")
            assertThat(it.Basic_Status.Power_Control.Power).isEqualTo("Standby")
            assertThat(it.Basic_Status.Power_Control.Sleep).isEqualTo(false)
            assertThat(it.Basic_Status.Party_Info).isEqualTo("Off") // boolean?
            assertThat(it.Basic_Status.Volume.Lvl.Val).isEqualTo(-400)
            assertThat(it.Basic_Status.Volume.Lvl.Exp).isEqualTo(1)
            assertThat(it.Basic_Status.Volume.Lvl.Unit).isEqualTo("dB")
            assertThat(it.Basic_Status.Volume.Mute).isEqualTo(false)
            assertThat(it.Basic_Status.Sound_Video).isNull()
        }
    }

    @Test
    fun testQueryStatus() {
        val xml = Persister(Format(0))
        val command = Command("GET", ZoneCommand("GetParam", null, null, null), null)
        val baos = ByteArrayOutputStream()
        xml.write(command, baos, "UTF-8")
        val commandString = String(baos.toByteArray(), StandardCharsets.UTF_8)
        assertThat(commandString).isEqualTo("<YAMAHA_AV cmd=\"GET\"><Main_Zone><Basic_Status>GetParam</Basic_Status></Main_Zone></YAMAHA_AV>")
    }

    @Test
    fun testPutPower() {
        val xml = Persister(Format(0))
        val command = Command("PUT", ZoneCommand(null, PowerControl("On", null), null, null), null)
        val baos = ByteArrayOutputStream()
        xml.write(command, baos, "UTF-8")
        val commandString = String(baos.toByteArray(), StandardCharsets.UTF_8)
        assertThat(commandString).isEqualTo("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Power_Control><Power>On</Power></Power_Control></Main_Zone></YAMAHA_AV>")
    }

    @Test
    fun testSelectInput() {
        val xml = Persister(Format(0))
        val command = Command("PUT", ZoneCommand(null, null, Input("Spotify"), null), null)
        val baos = ByteArrayOutputStream()
        xml.write(command, baos, "UTF-8")
        val commandString = String(baos.toByteArray(), StandardCharsets.UTF_8)
        assertThat(commandString).isEqualTo("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Input><Input_Sel>Spotify</Input_Sel></Input></Main_Zone></YAMAHA_AV>")
    }

    @Test
    fun testPutVolume() {
        val xml = Persister(Format(0))
        val command = Command("PUT", ZoneCommand(null, null, null, Volume(VolumeLevel(-600, 1, "dB"), null)), null)
        val baos = ByteArrayOutputStream()
        xml.write(command, baos, "UTF-8")
        val commandString = String(baos.toByteArray(), StandardCharsets.UTF_8)
        assertThat(commandString).isEqualTo("<YAMAHA_AV cmd=\"PUT\"><Main_Zone><Volume><Lvl><Val>-600</Val><Exp>1</Exp><Unit>dB</Unit></Lvl></Volume></Main_Zone></YAMAHA_AV>")
    }
}