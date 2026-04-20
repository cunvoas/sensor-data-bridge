pub mod ttn_uplink_message;

use actix_web::{post, web, App, HttpResponse, HttpServer, Responder};
use log::{info, error};
use dotenv::dotenv;
use std::env;
use std::sync::Arc;
use tokio::signal;
use tokio_postgres::{Client, NoTls};
use ttn_uplink_message::TtnUplinkMessage;

#[post("/uplink")]
async fn uplink_handler(
    msg: web::Json<TtnUplinkMessage>,
    db: web::Data<Arc<Client>>,
) -> impl Responder {
    let m = msg.into_inner();
    info!("[uplink_handler] Reçu uplink: app_id={}, dev_id={}, dev_eui={}", m.app_id(), m.dev_id(), m.dev_eui());
    let res = db.execute(
        "INSERT INTO ttn_uplink (app_id, dev_id, dev_eui, raw_payload, decoded_fields, port, rssi, snr, sf) VALUES ($1,$2,$3,$4::jsonb,$5,$6,$7,$8,$9)",
        &[&m.app_id(), &m.dev_id(), &m.dev_eui(), &m.raw_payload(), &m.decoded_fields(), &m.port(), &m.rssi(), &m.snr(), &m.sf()]
    ).await;
    match res {
        Ok(_) => {
            info!("[uplink_handler] Insert réussi pour app_id={}, dev_id={}", m.app_id(), m.dev_id());
            HttpResponse::Ok().body("Saved")
        },
        Err(e) => {
            error!("[uplink_handler] Erreur d'insert pour app_id={}, dev_id={}: {}", m.app_id(), m.dev_id(), e);
            HttpResponse::InternalServerError().body(format!("DB error: {}", e))
        },
    }
}

#[actix_web::main]
async fn main() -> std::io::Result<()> {
    dotenv().ok();
    env_logger::init();
    let host = env::var("POSTGRES_HOST").unwrap_or_else(|_| "localhost".to_string());
    let user = env::var("POSTGRES_USER").unwrap_or_else(|_| "postgres".to_string());
    let password = env::var("POSTGRES_PASSWORD").unwrap_or_else(|_| "postgres".to_string());
    let db = env::var("POSTGRES_DB").unwrap_or_else(|_| "ttn".to_string());
    let port = env::var("POSTGRES_PORT").ok().and_then(|p| if p.is_empty() { None } else { Some(p) }).unwrap_or_else(|| "5432".to_string());
    let conn_str = format!("host={} user={} password={} dbname={} port={}", host, user, password, db, port);
    let (client, connection) = tokio_postgres::connect(&conn_str, NoTls)
        .await.expect("DB connect");
    tokio::spawn(async move {
        if let Err(e) = connection.await {
            eprintln!("connection error: {}", e);
        }
    });
    let db = Arc::new(client);
    let server = HttpServer::new(move || {
        App::new()
            .app_data(web::Data::new(db.clone()))
            .service(uplink_handler)
    })
    .bind(("0.0.0.0", 8080))?
    .run();

    let srv_handle = server.handle();
    // Tâche pour gérer SIGTERM/SIGINT
    let signal_task = tokio::spawn(async move {
        signal::ctrl_c().await.expect("Failed to install signal handler");
        println!("SIGTERM/SIGINT reçu, arrêt du serveur...");
        srv_handle.stop(true).await;
    });
    // Attend la fin du serveur ou du signal
    let _ = tokio::join!(server, signal_task);
    Ok(())
}