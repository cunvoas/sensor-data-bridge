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

#[cfg(test)]
mod tests {
    use super::*;
    use serde_json::json;

    #[test]
    fn test_constructor_and_accessors() {
        let msg = TtnUplinkMessage::new(
            "app1".to_string(),
            "dev1".to_string(),
            "eui1".to_string(),
            json!({"foo": 42}),
            "{\"bar\":123}".to_string(),
            10,
        );
        assert_eq!(msg.app_id(), "app1");
        assert_eq!(msg.dev_id(), "dev1");
        assert_eq!(msg.dev_eui(), "eui1");
        assert_eq!(msg.raw_payload(), &json!({"foo": 42}));
        assert_eq!(msg.decoded_fields(), "{\"bar\":123}");
        assert_eq!(msg.port(), 10);
        assert_eq!(msg.rssi(), None);
        assert_eq!(msg.snr(), None);
        assert_eq!(msg.sf(), None);
    }

    #[test]
    fn test_set_radio_params() {
        let mut msg = TtnUplinkMessage::new(
            "app2".to_string(),
            "dev2".to_string(),
            "eui2".to_string(),
            json!(null),
            "{}".to_string(),
            2,
        );
        msg.set_radio_params(-120.5, 8.1, 12);
        assert_eq!(msg.rssi(), Some(-120.5));
        assert_eq!(msg.snr(), Some(8.1));
        assert_eq!(msg.sf(), Some(12));
    }

    #[test]
    fn test_display() {
        let msg = TtnUplinkMessage::new(
            "foo".to_string(),
            "bar".to_string(),
            "baz".to_string(),
            json!([1,2,3]),
            "{\"x\":1}".to_string(),
            5,
        );
        let s = format!("{}", msg);
        assert!(s.contains("foo/bar"));
        assert!(s.contains("data:'[1,2,3]'"));
        assert!(s.contains("fields:'{\"x\":1}'"));
    }

    #[test]
    fn test_serde_roundtrip() {
        let mut msg = TtnUplinkMessage::new(
            "app1".to_string(),
            "dev1".to_string(),
            "eui1".to_string(),
            json!("payload"),
            "fields".to_string(),
            18,
        );
        msg.set_radio_params(-100.0, 7.5, 9);
        let encoded = serde_json::to_string(&msg).unwrap();
        let decoded: TtnUplinkMessage = serde_json::from_str(&encoded).unwrap();
        assert_eq!(decoded.app_id(), "app1");
        assert_eq!(decoded.dev_id(), "dev1");
        assert_eq!(decoded.dev_eui(), "eui1");
        assert_eq!(decoded.rssi(), Some(-100.0));
        assert_eq!(decoded.snr(), Some(7.5));
        assert_eq!(decoded.sf(), Some(9));
    }
}
