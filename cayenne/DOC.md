# Module : cayenne

---

## 🇫🇷 FRANÇAIS — Documentation du module

### Description
Ce module fournit les outils pour formater, décoder et encoder des messages au format Cayenne LPP (Low Power Payload). Il encapsule des classes métier, exceptions, et des interfaces outillage pour le formatage numérique, GPS, etc.

### Dépendances principales
- **Logger** : SLF4J/reload4j (utilisé pour les tests)
- Pas de dépendances métier externes (modulaire, dédié Cayenne)

### Instructions de build
```sh
mvn clean package
```

### Génération de la Javadoc
```sh
mvn javadoc:javadoc
```
La documentation sera générée dans `target/site/apidocs/index.html`.

### Structure notable
- `nl.sikken.bertrik.cayenne` : classes Cayenne principales (CayenneItem, CayenneMessage, ECayennePayloadFormat)
- `nl.sikken.bertrik.cayenne.formatter` : formatteurs numériques, GPS, Float, Int

### Tests
Lancez les tests (si disponibles) avec :
```sh
mvn test
```

---

## 🇬🇧 ENGLISH — Module Documentation

### Description
This module provides tools for formatting, decoding, and encoding Cayenne LPP (Low Power Payload) messages. It encapsulates business classes, exception types, and utility interfaces for numeric, GPS, and other data formatting.

### Main dependencies
- **Logger**: SLF4J/reload4j (used for tests)
- No external business dependencies (modular, Cayenne-dedicated)

### Build instructions
```sh
mvn clean package
```

### Javadoc generation
```sh
mvn javadoc:javadoc
```
Documentation will be available at `target/site/apidocs/index.html`.

### Notable structure
- `nl.sikken.bertrik.cayenne`: main Cayenne classes (CayenneItem, CayenneMessage, ECayennePayloadFormat)
- `nl.sikken.bertrik.cayenne.formatter`: numeric/GPS/Float/Int formatters

### Tests
Run module tests (if implemented) with:
```sh
mvn test
```
