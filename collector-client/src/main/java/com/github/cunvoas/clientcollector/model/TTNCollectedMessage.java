package com.github.cunvoas.clientcollector.model;

public class TTNCollectedMessage {

    private final String appId;
    private final String devId;
    private final String devEui;
    private final byte[] rawPayload;
    private final String decodedFields;
    private final int port;
    private Double rssi = Double.NaN;
    private Double snr = Double.NaN;
    private Integer sf = 0;

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

    public String getAppId() {
        return appId;
    }

    public String getDevId() {
        return devId;
    }

    public String getDevEui() {
        return devEui;
    }

    public byte[] getRawPayload() {
        return rawPayload.clone();
    }

    public String getDecodedFields() {
        return decodedFields;
    }

    public int getPort() {
        return port;
    }

    public Double getRSSI() {
        return rssi;
    }

    public Double getSNR() {
        return snr;
    }

    public Integer getSF() {
        return sf;
    }

}
