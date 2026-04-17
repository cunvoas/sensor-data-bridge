# sensor-data-bridge

## 🟦 Français

Logiciel passerelle pour recevoir les données de particules en suspension depuis TheThingsNetwork et les transmettre à sensor.community, opensense, etc.

L’envoi vers sensor.community et d’autres tableaux de bord se contrôle avec les attributs TTN de l’appareil, configurables individuellement dans la console TheThingsNetwork :
- sensor.community
  - l’attribut `senscom-id` contient l’identifiant matériel sensor.community
- opensense.org
  - l’attribut `opensense-id`

Plusieurs codages de données sont supportés :
- Encodage Cayenne : les PM sont encodées comme données analogiques (PM10 : canal 1, PM2.5 : canal 2, PM4.0 : canal 4, PM1.0 : canal 0)
- Encodage JSON : un fichier de configuration indique comment chaque champ JSON correspond à une propriété de mesure
- Encodage SPS30 : un encodage spécifique pour les capteurs SPS30, comprend les comptes particulaires

Support expérimental pour Helium LoRaWAN, NB-IOT. Prise en charge future possible pour Sigfox.

---

## 🟧 English

Bridge software for receiving airborne particulate matter data from TheThingsNetwork and forwarding it to sensor.community, opensense, etc.

Forwarding to sensor.community and other dashboards is controlled through TTN device attributes, which can be configured for each device in the TheThingsNetwork console:
- sensor.community
  - attribute `senscom-id` contains the sensor.community hardware ID
- opensense.org
  - attribute `opensense-id`

Several data encodings are supported:
- Cayenne encoding: PM is encoded as analog data (PM10: channel 1, PM2.5: channel 2, PM4.0: channel 4, PM1.0: channel 0)
- JSON encoding: the configuration file specifies how each JSON field maps to a measurement property
- SPS30 encoding: a custom encoding for SPS30 sensors, includes the particle count

Experimental support for Helium LoRaWAN, NB-IOT. Possible future support for Sigfox.

---

## 🇫🇷 Configuration

### Configuration TheThingsNetwork

L’application nécessite une clé API d’application avec les droits suivants :

- Voir les appareils de l’application
- Voir les informations sur l’application
- Lire le trafic de l’application (uplink et downlink)

![TTN API key](ttn-api-key.png)

Pensez à copier/sauvegarder cette clé dès sa création dans la console TTN.

### Configuration de l’application

Exemple de fichier de configuration (YAML) :

~~~~
---
ttn:
  mqtt_url: "tcp://eu1.cloud.thethings.network"
  identity_server_url: "https://eu1.cloud.thethings.network"
  identity_server_timeout: 30
  apps:
  - name: "particulatematter"
    key: "secret"
    decoder:
      encoding: "CAYENNE"
      properties: ""
  - name: "ttn-hittestress"
    key: "secret"
    decoder:
      encoding: "JSON"
      properties:
      - path: "/pm10"
        item: "PM10"
      - path: "/pm2p5"
        item: "PM2_5"
      - path: "/rh"
        item: "HUMIDITY"
      - path: "/temp"
        item: "TEMPERATURE"
  - name: "ttn-soundkit"
    key: "secret"
    decoder:
      encoding: "JSON"
      properties:
      - path: "/la/min"
        item: "NOISE_LA_MIN"
      - path: "/la/avg"
        item: "NOISE_LA_EQ"
      - path: "/la/max"
        item: "NOISE_LA_MAX"
nbiot:
  port: 9000
senscom:
  url: "https://api.sensor.community"
  timeout: 30
opensense:
  url: "https://api.opensensemap.org"
  timeout: 30
geolocation:
  url: "https://location.services.mozilla.com"
  timeout: 30
  apikey: "test"
~~~~

---

## 🇬🇧 Configuration

### TheThingsNetwork configuration

The application requires an application API key with the following individual rights:

- View devices in application
- View application information
- Read application traffic (uplink and downlink)

![TTN API key](ttn-api-key.png)

Make sure to copy/save it to another place immediately after creating it on the TTN console.

### Application configuration

Application example config file (YAML):

~~~~
---
ttn:
  mqtt_url: "tcp://eu1.cloud.thethings.network"
  identity_server_url: "https://eu1.cloud.thethings.network"
  identity_server_timeout: 30
  apps:
  - name: "particulatematter"
    key: "secret"
    decoder:
      encoding: "CAYENNE"
      properties: ""
  - name: "ttn-hittestress"
    key: "secret"
    decoder:
      encoding: "JSON"
      properties:
      - path: "/pm10"
        item: "PM10"
      - path: "/pm2p5"
        item: "PM2_5"
      - path: "/rh"
        item: "HUMIDITY"
      - path: "/temp"
        item: "TEMPERATURE"
  - name: "ttn-soundkit"
    key: "secret"
    decoder:
      encoding: "JSON"
      properties:
      - path: "/la/min"
        item: "NOISE_LA_MIN"
      - path: "/la/avg"
        item: "NOISE_LA_EQ"
      - path: "/la/max"
        item: "NOISE_LA_MAX"
nbiot:
  port: 9000
senscom:
  url: "https://api.sensor.community"
  timeout: 30
opensense:
  url: "https://api.opensensemap.org"
  timeout: 30
geolocation:
  url: "https://location.services.mozilla.com"
  timeout: 30
  apikey: "test"
~~~~
