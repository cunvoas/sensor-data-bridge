use rust_collector::ttn_uplink_message::TtnUplinkMessage;
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