# Dockerfile multi-stage pour rust-collector
FROM rust:1.77 as builder
WORKDIR /app
COPY . .
RUN cargo build --release

FROM debian:bookworm-slim
WORKDIR /app
COPY --from=builder /app/target/release/rust-collector /usr/local/bin/rust-collector
COPY .env.example .env
COPY ttn_uplink.sql ./
EXPOSE 8080
CMD ["/usr/local/bin/rust-collector"]
