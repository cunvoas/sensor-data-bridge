# Architecture technique – sensor-data-bridge

## 1. Vue d’ensemble

Le module `sensor-data-bridge` est le cœur de l’application. Il fait l’interface entre différents réseaux IoT (LoRa, TTN, Helium, NB-IoT), effectue le décodage des messages capteurs reçus, gère les interactions avec les plateformes Cloud (OpenSense, SensCom, etc.), expose des APIs REST d’intégration (Jersey) et communique des données via MQTT.

### Schéma d’architecture globale

```mermaid
graph TD
    subgraph Réseaux_IoT
        TTN[TTN]
        Helium[Helium]
        NBIoT[NB-IoT]
    end
    subgraph Bridge
        MqttListener[MqttListener]
        SensorDataBridge[SensorDataBridge]
        Decoder[Decoder (Cayenne/ULM/JSON)]
        Uploaders[Uploaders (SensCom/OpenSense)]
        RestAPI[REST API (Jersey)]
    end
    subgraph Plateformes_Cloud
        SensCom[SensCom]
        OpenSense[OpenSense]
    end
    TTN --> MqttListener
    Helium --> MqttListener
    NBIoT --> MqttListener
    MqttListener --> SensorDataBridge
    SensorDataBridge --> Decoder
    SensorDataBridge --> Uploaders
    SensorDataBridge --> RestAPI
    Uploaders --> SensCom
    Uploaders --> OpenSense
```

## 2. Composants principaux

- **MqttListener** : Écoute les messages uplink des réseaux IoT (TTN, Helium, NB-IoT).
- **SensorDataBridge** : Point d’entrée principal, orchestre le décodage et la distribution des données.
- **Decoder** : Décode les payloads capteurs (Cayenne LPP, ULM, JSON).
- **Uploaders** : Gère l’envoi des données structurées vers les plateformes cloud (SensCom, OpenSense).
- **REST API** : Expose des endpoints pour l’intégration et la consultation des données.

## 3. Diagrammes de séquence

### 3.1 Flux uplink principal

```mermaid
sequenceDiagram
    participant TTN as Réseau TTN (MQTT)
    participant MqttListener
    participant SensorDataBridge
    participant Decoder as Décodeur (Cayenne/ULM/JSON)
    participant Uploaders as Uploaders (SensCom/OpenSense)

    Note over TTN: 1. Message uplink LoRaWAN reçu
    TTN->>MqttListener: uplink (payload capteur)
    MqttListener->>SensorDataBridge: messageReceived()
    SensorDataBridge->>Decoder: decodeTtnMessage()
    Decoder-->>SensorDataBridge: SensorData (structurée)
    SensorDataBridge->>Uploaders: scheduleUpload(SensorData)
    Uploaders-->>Cloud: Envoi vers plateformes cloud
    Note over SensorDataBridge: (optionnel) Synchronisation device registry
```

### 3.2 Flux API REST (consultation d’une mesure)

```mermaid
sequenceDiagram
    participant Client as Client REST
    participant RestAPI as REST API (Jersey)
    participant SensorDataBridge
    participant Storage as Storage/Cache

    Client->>RestAPI: GET /sensors/{id}/last
    RestAPI->>SensorDataBridge: getLastSensorData(id)
    SensorDataBridge->>Storage: fetchLastData(id)
    Storage-->>SensorDataBridge: SensorData
    SensorDataBridge-->>RestAPI: SensorData
    RestAPI-->>Client: JSON (SensorData)
```

### 3.3 Synchronisation du registre des devices

```mermaid
sequenceDiagram
    participant Scheduler as Scheduler
    participant SensorDataBridge
    participant DeviceRegistry as Device Registry
    participant Cloud as Plateforme Cloud

    Scheduler->>SensorDataBridge: triggerSync()
    SensorDataBridge->>DeviceRegistry: fetchLocalDevices()
    DeviceRegistry-->>SensorDataBridge: DeviceList
    SensorDataBridge->>Cloud: fetchCloudDevices()
    Cloud-->>SensorDataBridge: CloudDeviceList
    SensorDataBridge->>DeviceRegistry: updateRegistry(diff)
```

---

Document généré automatiquement le 2026-04-28.
