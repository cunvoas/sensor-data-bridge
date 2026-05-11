package nl.bertriksikken.loraforwarder;

import java.util.Map;

import nl.bertriksikken.pm.SensorData;

/**
 * Interface for uploaders that receive sensor data and forward it to external
 * services (e.g. sensor.community, OpenSense).
 */
public interface IUploader {

    void start();

    void stop();

    void scheduleProcessAttributes(String applicationId, Map<String, AttributeMap> deviceAttributes);

    void scheduleUpload(AppDeviceId appDeviceId, SensorData data);

}
