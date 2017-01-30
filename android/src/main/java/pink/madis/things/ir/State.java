package pink.madis.things.ir;

/**
 * Represents a state my home entertainment system can be in
 */
public enum State {
    TELIA("HDMI1", Device.AVRECEIVER, Device.TV, Device.IPTV_STB),
    PS4("HDMI2", Device.AVRECEIVER, Device.TV, Device.PS4),
    SPOTIFY("Spotify", Device.AVRECEIVER),
    PHONO("PHONO", Device.AVRECEIVER),
    RADIO("TUNER", Device.AVRECEIVER);

    private final String input;
    private final Device[] devices;

    State(String input, Device... requiredDevices) {
        this.input = input;
        this.devices = requiredDevices;
    }
}
