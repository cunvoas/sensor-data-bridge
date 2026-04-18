// src/ttn_uplink_message.rs
// Equivalent Rust de la classe Java TtnUplinkMessage
// Représente un message uplink TTN avec les champs principaux

use serde::{Deserialize, Serialize};
use serde_json::Value;
use std::fmt;

#[derive(Clone, Debug, Serialize, Deserialize)]
/// Structure représentant un message TTN uplink
pub struct TtnUplinkMessage {
    /// Identifiant de l'application TTN
    app_id: String,
    /// Identifiant du device TTN
    dev_id: String,
    /// EUI du device TTN
    dev_eui: String,
    /// Payload brut (JSON natif)
    raw_payload: Value,
    /// Champs décodés (JSON sérialisé en String)
    decoded_fields: String,
    /// Port LoRa utilisé
    port: i32,
    /// Paramètres radio (optionnels)
    rssi: Option<f64>,
    snr: Option<f64>,
    sf: Option<i32>,
}

#[allow(dead_code)]
impl TtnUplinkMessage {
    /// Constructeur principal
    pub fn new(app_id: String, dev_id: String, dev_eui: String, raw_payload: Value, decoded_fields: String, port: i32) -> Self {
        Self {
            app_id,
            dev_id,
            dev_eui,
            raw_payload,
            decoded_fields,
            port,
            rssi: None,
            snr: None,
            sf: None,
        }
    }

    /// Définit les paramètres radio (RSSI, SNR, SF)
    pub fn set_radio_params(&mut self, rssi: f64, snr: f64, sf: i32) {
        self.rssi = Some(rssi);
        self.snr = Some(snr);
        self.sf = Some(sf);
    }

    // Accesseurs pour chaque champ
    pub fn app_id(&self) -> &str { &self.app_id }
    pub fn dev_id(&self) -> &str { &self.dev_id }
    pub fn dev_eui(&self) -> &str { &self.dev_eui }
    pub fn raw_payload(&self) -> &Value { &self.raw_payload }
    pub fn decoded_fields(&self) -> &str { &self.decoded_fields }
    pub fn port(&self) -> i32 { self.port }
    pub fn rssi(&self) -> Option<f64> { self.rssi }
    pub fn snr(&self) -> Option<f64> { self.snr }
    pub fn sf(&self) -> Option<i32> { self.sf }
}

impl fmt::Display for TtnUplinkMessage {
    /// Affichage formaté du message (similaire à toString en Java)
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        write!(f, "{}/{}: {{data:'{}', fields:'{}'}}", 
            self.app_id, 
            self.dev_id, 
            self.raw_payload, 
            self.decoded_fields)
    }
}