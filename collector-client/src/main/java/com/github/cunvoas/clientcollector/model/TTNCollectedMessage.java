package com.github.cunvoas.clientcollector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TTNCollectedMessage {
    @JsonProperty("app_id")
    private final String appId;
    @JsonProperty("dev_id")
    private final String devId;
    @JsonProperty("dev_eui")
    private final String devEui;
    private final byte[] rawPayload;
    @JsonProperty("decoded_fields")
    private final String decodedFields;
    @JsonProperty("port")
    private final int port;
    @JsonProperty("rssi")
    private Double rssi = null;
    @JsonProperty("snr")
    private Double snr = null;
    @JsonProperty("sf")
    private Integer sf = null;

    public TTNCollectedMessage(String appId, String devId, String devEui, byte[] rawPayload, String decodedFields,
            int port) {
        this.appId = appId;
        this.devId = devId;
        this.devEui = devEui;
        this.rawPayload = rawPayload.clone();
        this.decodedFields = decodedFields;
        this.port = port;
    }

    public void setRadioParams(Double rssi, Double snr, Integer sf) {
        this.rssi = rssi;
        this.snr = snr;
        this.sf = sf;
    }

    public String getAppId() { return appId; }
    public String getDevId() { return devId; }
    public String getDevEui() { return devEui; }
    public String getDecodedFields() { return decodedFields; }
    public int getPort() { return port; }
    public Double getRSSI() { return rssi; }
    public Double getSNR() { return snr; }
    public Integer getSF() { return sf; }

    @JsonProperty("raw_payload")
    public int[] getRawPayloadArray() {
        int[] arr = new int[rawPayload.length];
        for (int i = 0; i < rawPayload.length; i++) {
            arr[i] = rawPayload[i] & 0xFF;
        }
        return arr;
    }
}