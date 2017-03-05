package pink.madis.things.ir

/**
 * Represents a device that can be turned on or off and queried for status.
 * All of the methods are synchronous, i.e., given no external actors:
 *
 * setState(x);
 * getState(); // returns x
 */
interface Device {
    var state: State
}
