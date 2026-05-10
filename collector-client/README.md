README - collector-client
=========================

But: expliquer comment configurer `collector-client` (fichier de propriétés externe ou ressource classpath).

Emplacement
-----------
Le module `collector-client` lit un fichier de configuration nommé `config.properties` pour récupérer notamment la clé `rust-collector.url` utilisée pour envoyer les POSTs vers le collecteur.

Modes de chargement
-------------------
Le client supporte deux méthodes de chargement, dans l'ordre de priorité :

1. Fichier externe indiqué via une propriété JVM :
   - `-Dconfig=/chemin/vers/config.properties`
   - `-Dconfig.file=/chemin/vers/config.properties`
   - `-Dconfig.properties=/chemin/vers/config.properties`

   Si l'une de ces propriétés est fournie et non vide, le chemin est utilisé tel quel pour ouvrir le fichier depuis le système de fichiers.

2. Ressource dans le classpath :
   - `config.properties` (placé par exemple dans `src/main/resources/`)

Exemples
--------

Exemple minimal de `config.properties` :

    rust-collector.url=http://localhost:8080/collect

Lancer l'application avec un fichier externe :

```sh
java -Dconfig=/etc/myapp/config.properties -jar myapp.jar
```

Ou avec la propriété alternative :

```sh
java -Dconfig.file=./config.properties -jar myapp.jar
```

Comportement en cas d'erreur
---------------------------
- Si aucun fichier n'est trouvé (ni via la propriété JVM, ni dans le classpath), le constructeur de `TTNCollectorClient` lèvera une `RuntimeException` expliquant que `config.properties` est introuvable.
- Si le fichier est trouvé mais que la clé `rust-collector.url` est absente, une `RuntimeException` est également levée pour signaler la clé manquante.

Bonnes pratiques
-----------------
- Préférez fournir un fichier externe en production (sécurité, facilité de déploiement).
- Placez un `config.properties` par défaut dans `src/main/resources/` pour les environnements de dev/local.
- Vérifiez les permissions d'accès au fichier lorsque vous utilisez un chemin absolu.

Dépannage rapide
----------------
- "config.properties introuvable" : vérifier la présence du fichier et la bonne propriété JVM.
- "La clé 'rust-collector.url' est absente" : ajouter `rust-collector.url` au fichier de configuration.
- Si l'application tourne dans un conteneur, montez le fichier de configuration dans l'image ou passez la propriété JVM au démarrage du conteneur.

Questions / amélioration
------------------------
Si vous souhaitez un comportement différent (par exemple utiliser une variable d'environnement, ignorer les erreurs et utiliser une URL par défaut, ou accepter un répertoire contenant `config.properties`), dites-le et je peux adapter l'implémentation et la documentation.
