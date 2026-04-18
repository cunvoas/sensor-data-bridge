package nl.bertriksikken.opensense;

import nl.bertriksikken.loraforwarder.AppDeviceId;
import nl.bertriksikken.loraforwarder.AttributeMap;
import nl.bertriksikken.pm.ESensorItem;
import nl.bertriksikken.pm.SensorData;
import nl.bertriksikken.senscom.SensComMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OpenSenseUploaderTest {
    private IOpenSenseRestApi restApi;
    private OpenSenseUploader uploader;

    @BeforeEach
    void setUp() {
        restApi = mock(IOpenSenseRestApi.class);
        uploader = new OpenSenseUploader(restApi);
    }

    @AfterEach
    void tearDown() {
        uploader.stop(); // shuts down thread pool
    }

    @Test
    void testConstructorRequiresRestApi() {
        assertThrows(NullPointerException.class, () -> new OpenSenseUploader(null));
    }

    @Test
    void testCreateFactory() {
        OpenSenseConfig config = new OpenSenseConfig();
        OpenSenseUploader instance = OpenSenseUploader.create(config);
        assertNotNull(instance);
    }

    @Test
    void testStartAndStop() {
        uploader.start(); // just logs, no exception
        uploader.stop(); // shuts down executor
        // Repeated stop should not throw
        assertDoesNotThrow(() -> uploader.stop());
    }

    @Test
    void testScheduleProcessAttributes_updatesBoxIds() throws InterruptedException {
        String appId = "someApp";
        String devId = "device1";
        String opensenseId = "os123";
        AttributeMap attrMap = new AttributeMap(Map.of("opensense-id", opensenseId));
        Map<String, AttributeMap> attrs = Map.of(devId, attrMap);
        uploader.scheduleProcessAttributes(appId, attrs);
        // Wait for async
        TimeUnit.MILLISECONDS.sleep(50);
        // Use scheduleUpload to check that mapping was performed
        SensorData data = new SensorData();
        data.putValue(ESensorItem.PM10, 10);
        uploader.scheduleUpload(new AppDeviceId(appId, devId), data);
        TimeUnit.MILLISECONDS.sleep(50);
        // We expect the restApi to be called as a result
        verify(restApi, atLeastOnce()).postNewMeasurements(anyString(), anyBoolean(), any(SensComMessage.class));
    }

    @Test
    void testScheduleUpload_notMappedDevice_noCall() throws InterruptedException {
        uploader.scheduleUpload(new AppDeviceId("foo", "bar"), new SensorData());
        // Wait for async (should not actually call)
        TimeUnit.MILLISECONDS.sleep(50);
        verify(restApi, never()).postNewMeasurements(anyString(), anyBoolean(), any());
    }

    @Test
    void testScheduleUpload_withPMValues_triggersApiCall() throws IOException, InterruptedException {
        // Insert mapping manually via processAttributes
        String appId = "a";
        String devId = "b";
        String opensenseId = "osId";
        AttributeMap attrMap = new AttributeMap(Map.of("opensense-id", opensenseId));
        uploader.scheduleProcessAttributes(appId, Map.of(devId, attrMap));
        TimeUnit.MILLISECONDS.sleep(50);
        AppDeviceId appDeviceId = new AppDeviceId(appId, devId);
        SensorData data = new SensorData();
        data.putValue(ESensorItem.PM10, 10.0);
        data.putValue(ESensorItem.PM2_5, 20.0);
        data.putValue(ESensorItem.PM1_0, 4.2);
        // Will trigger PMS_ prefix
        uploader.scheduleUpload(appDeviceId, data);
        // Prepare mock REST response
        Call<String> callMock = mock(Call.class);
        when(restApi.postNewMeasurements(any(), anyBoolean(), any())).thenReturn(callMock);
        when(callMock.execute()).thenReturn(Response.success("ok"));
        // Allow async
        TimeUnit.MILLISECONDS.sleep(100);
        verify(restApi, atLeastOnce()).postNewMeasurements(eq(opensenseId), eq(true), any(SensComMessage.class));
    }

    @Test
    void testScheduleUpload_errorInRestClientHandled() throws IOException, InterruptedException {
        String appId = "foo";
        String devId = "bar";
        String opensenseId = "osIdXXX";
        AttributeMap attrMap = new AttributeMap(Map.of("opensense-id", opensenseId));
        uploader.scheduleProcessAttributes(appId, Map.of(devId, attrMap));
        TimeUnit.MILLISECONDS.sleep(50);
        AppDeviceId appDeviceId = new AppDeviceId(appId, devId);
        SensorData data = new SensorData();
        data.putValue(ESensorItem.PM10, 1.1);
        // REST throws exception
        Call<String> callMock = mock(Call.class);
        when(restApi.postNewMeasurements(any(), anyBoolean(), any())).thenReturn(callMock);
        when(callMock.execute()).thenThrow(new IOException("fail"));
        uploader.scheduleUpload(appDeviceId, data);
        TimeUnit.MILLISECONDS.sleep(100);
        verify(restApi, atLeastOnce()).postNewMeasurements(eq(opensenseId), eq(true), any(SensComMessage.class));
    }

    @Test
    void testScheduleUpload_withMeteoValues() throws InterruptedException {
        String appId = "foo";
        String devId = "bar";
        String opensenseId = "osIdYYY";
        AttributeMap attrMap = new AttributeMap(Map.of("opensense-id", opensenseId));
        uploader.scheduleProcessAttributes(appId, Map.of(devId, attrMap));
        TimeUnit.MILLISECONDS.sleep(50);
        AppDeviceId appDeviceId = new AppDeviceId(appId, devId);
        SensorData data = new SensorData();
        data.putValue(ESensorItem.HUMIDITY, 22.5);
        data.putValue(ESensorItem.TEMPERATURE, 15.8);
        data.putValue(ESensorItem.PRESSURE, 100000);
        uploader.scheduleUpload(appDeviceId, data);
        TimeUnit.MILLISECONDS.sleep(100);
        verify(restApi, atLeastOnce()).postNewMeasurements(eq(opensenseId), eq(true), any(SensComMessage.class));
    }
}
