#!/bin/sh
# Script d'audit Sonar pour projet Rust
# 1. Lint (clippy)
cargo clippy --message-format=json > clippy-report.json || exit 1
# 2. Couverture (tarpaulin)
cargo tarpaulin --out Lcov --output-dir . || exit 1
# 3. Analyse Sonar
sonar-scanner
