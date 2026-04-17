# Configuration sécurisée de l'API key pour OWASP dependency-check

Ce document explique comment configurer de manière sécurisée la clé API NVD pour le plugin OWASP dependency-check.

## Obtention de la clé API NVD

1. **Créer un compte** sur le site officiel du NVD : https://nvd.nist.gov/developers/request-an-api-key
2. **Remplir le formulaire** de demande de clé API
3. **Attendre l'approbation** (généralement quelques jours)
4. **Recevoir la clé API** par email

## Configuration sécurisée

### Option 1 : Variable d'environnement (Recommandée)

La clé API est configurée via la variable d'environnement `NVD_API_KEY` :

```bash
# Linux/Mac
export NVD_API_KEY="votre-clé-api-nvd-ici"

# Windows
set NVD_API_KEY=votre-clé-api-nvd-ici
```

### Option 2 : Fichier .env local (pour développement)

Créer un fichier `.env` dans le répertoire racine du projet :

```bash
# .env
NVD_API_KEY=votre-clé-api-nvd-ici
```

**⚠️ Important** : Ajouter `.env` au fichier `.gitignore` pour éviter de versionner la clé !

### Option 3 : Properties Maven locales

Configurer dans `~/.m2/settings.xml` :

```xml
<settings>
  <profiles>
    <profile>
      <id>security-keys</id>
      <properties>
        <env.NVD_API_KEY>votre-clé-api-nvd-ici</env.NVD_API_KEY>
      </properties>
    </profile>
  </profiles>
  
  <activeProfiles>
    <activeProfile>security-keys</activeProfile>
  </activeProfiles>
</settings>
```

## Utilisation

### Exécution du scan de sécurité

```bash
# Avec la clé API configurée
mvn clean compile -POWASP dependency-check:check

# Vérification des vulnérabilités uniquement
mvn dependency-check:check -POWASP
```

### Configuration CI/CD

#### GitHub Actions

```yaml
- name: Run OWASP Dependency Check
  env:
    NVD_API_KEY: ${{ secrets.NVD_API_KEY }}
  run: mvn clean compile -POWASP dependency-check:check
```

#### GitLab CI

```yaml
security_scan:
  script:
    - mvn clean compile -POWASP dependency-check:check
  variables:
    NVD_API_KEY: $NVD_API_KEY
```

## Configuration actuelle du plugin

Le plugin est configuré avec les paramètres suivants :

- **failBuildOnCVSS**: 8 (échec du build si CVSS >= 8.0)
- **nvdApiDelay**: 4000ms (délai entre les requêtes API)
- **connectionTimeout**: 30000ms (timeout de connexion)
- **Formats de rapport**: HTML et JSON
- **Répertoire de sortie**: `target/dependency-check-report/`

## Avantages de l'utilisation d'une clé API

1. **Performance améliorée** : Pas de limitation de rate limiting
2. **Fiabilité** : Moins de timeouts et d'erreurs de connexion
3. **Données à jour** : Accès prioritaire aux dernières données de vulnérabilités

## Sécurité

### ✅ Bonnes pratiques

- Utiliser des variables d'environnement
- Ne jamais versionner les clés dans le code source
- Utiliser des secrets dans les systèmes CI/CD
- Limiter l'accès aux clés aux personnes autorisées

### ❌ À éviter

- Hardcoder la clé dans le `pom.xml`
- Committer des fichiers contenant des clés
- Partager les clés par email ou chat
- Utiliser la même clé pour plusieurs projets

## Dépannage

### Erreur "API key required"
- Vérifiez que la variable `NVD_API_KEY` est bien définie
- Testez avec : `echo $NVD_API_KEY`

### Erreur "Invalid API key"
- Vérifiez que la clé est correcte
- Assurez-vous qu'elle est approuvée par le NVD

### Timeouts fréquents
- Augmentez `nvdApiDelay` à 6000ms ou plus
- Augmentez `connectionTimeout` si nécessaire

## Ressources

- [Documentation officielle OWASP dependency-check](https://jeremylong.github.io/DependencyCheck/)
- [API NVD documentation](https://nvd.nist.gov/developers)
- [Maven Security Best Practices](https://maven.apache.org/guides/mini/guide-encryption.html)