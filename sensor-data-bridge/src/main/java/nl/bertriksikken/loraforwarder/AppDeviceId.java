package nl.bertriksikken.loraforwarder;

/**
 * Combination of application name and device id that uniquely identifies a device.
 * This record is suitable for use as a map/set key.
 */
public record AppDeviceId(String appName, String deviceId) {

}
