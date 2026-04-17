# Documentation centrale / Main Documentation

---

## 🇫🇷 Accueil de la documentation

Ce projet Maven est structuré en plusieurs modules spécialisés. La documentation complète est organisée par module, chacun possédant son propre fichier DOC.md détaillé :

- [DOC du module sensor-data-bridge](sensor-data-bridge/DOC.md)  
  Module principal : interfaces réseaux IoT (LoRa, TTN, Helium, NB-IoT), décodage messages capteurs, REST APIs, intégration Cloud (OpenSense, SensCom), gestion MQTT…

- [DOC du module cayenne](cayenne/DOC.md)  
  Librairie interne pour le format Cayenne LPP (Low Power Payload) : décodage/encodage, structure métier, formatteurs numériques/GPS, sans dépendances externes majeures.

Vous trouverez dans chaque DOC de module :
- Description fonctionnelle
- Dépendances
- Instructions d’installation, build, test, Javadoc
- Diagrammes métier Mermaid (FR+EN)
- Structure interne et points d’extension

Consultez les modules selon votre usage :
- Développeur applicatif : commencez par [sensor-data-bridge/DOC.md](sensor-data-bridge/DOC.md)
- Intégration protocole/message bas-niveau : voir [cayenne/DOC.md](cayenne/DOC.md)

**Tous les commentaires et la documentation sont bilingues (français/anglais) : merci de veiller à conserver ce standard lors de toute contribution.**

---

## 🇬🇧 Documentation welcome

This Maven project is structured into specialized modules. Full documentation is organized per module, each with its own detailed DOC.md:

- [sensor-data-bridge module DOC](sensor-data-bridge/DOC.md)  
  Main module: interfaces with IoT networks (LoRa, TTN, Helium, NB-IoT), sensor message decoding, REST APIs, Cloud integration (OpenSense, SensCom), MQTT handling…

- [cayenne module DOC](cayenne/DOC.md)  
  Internal library for the Cayenne LPP (Low Power Payload) format: decoding/encoding, business structure, numeric/GPS formatters, no major external dependencies.

Each module DOC contains:
- Functional description
- Dependencies
- Build, install, test, Javadoc instructions
- Mermaid business diagrams (FR+EN)
- Internal structure and extension overview

Choose modules based on your purpose:
- Application developer: start with [sensor-data-bridge/DOC.md](sensor-data-bridge/DOC.md)
- Protocol/low-level message integration: see [cayenne/DOC.md](cayenne/DOC.md)

**All comments and documentation are bilingual (French/English): please help preserve this quality standard when contributing.**
