package pink.madis.things.ir

/**
 * Represents a device that can be turned on or off and queried for status.
 */
interface Device {
    var state: State
}
