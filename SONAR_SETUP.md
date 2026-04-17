# SonarQube Authentication Setup

## Problem
The SonarQube analysis was failing with the error:
```
Not authorized. Analyzing this project requires authentication. Please check the user token in the property 'sonar.token' or 'sonar.login' (deprecated).
```

## Solution

### Configuration Files

The SonarQube configuration is now split between two files for optimal organization:

#### 1. **pom.xml** (Essential Maven properties)
- Server connection: `sonar.host.url`, `sonar.organization`, `sonar.projectKey`
- Authentication: `sonar.token` (runtime parameter)
- Basic coverage configuration: `sonar.java.coveragePlugin`

#### 2. **sonar-project.properties** (Detailed analysis configuration)
- Project information and encoding
- Java language settings
- Coverage and test report paths
- Static analysis tool integration
- Exclusion patterns for analysis
- Quality gate configurations

This separation ensures that Maven can properly connect to your SonarQube server while keeping detailed configuration organized in an external file.

### 1. Generate a SonarQube Token

1. **Access your SonarQube server**: Open http://192.168.1.20:9000 in your browser
2. **Login** to your SonarQube instance
3. **Navigate to token generation**:
   - Go to **Administration** → **Security** → **Users**
   - Find your user account and click on the **Tokens** column
   - Or go to your user profile → **Security** tab
4. **Generate a new token**:
   - Enter a name for your token (e.g., "sensor-atmo-ci" or "maven-analysis")
   - Set an expiration date (optional)
   - Click **Generate**
5. **Copy the token** immediately (it will only be shown once)

### 2. Use the Token with Maven

You have several options to provide the token:

#### Option 1: Command Line (Recommended)
```bash
mvn clean compile sonar:sonar -Psonar -Dsonar.token=YOUR_GENERATED_TOKEN
```

#### Option 2: Maven Settings.xml
Add to your `~/.m2/settings.xml`:
```xml
<settings>
    <profiles>
        <profile>
            <id>sonar</id>
            <properties>
                <sonar.token>YOUR_GENERATED_TOKEN</sonar.token>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>sonar</activeProfile>
    </activeProfiles>
</settings>
```

#### Option 3: Environment Variable
```bash
export SONAR_TOKEN=YOUR_GENERATED_TOKEN
mvn clean compile sonar:sonar -Psonar -Dsonar.token=$SONAR_TOKEN
```

### 3. Run the Analysis

With the token configured, run:
```bash
mvn clean compile sonar:sonar -Psonar -Dsonar.token=YOUR_TOKEN
```

### 4. Configuration Details

#### **Server Configuration** (in pom.xml)
- **Host**: http://192.168.1.20:9000
- **Organization**: LM Oxygène
- **Project Key**: sensor-atmo

#### **Analysis Configuration** (in sonar-project.properties)
- Comprehensive exclusion patterns for generated code, tests, and DTOs
- Multi-module support for `cayenne` and `sensor-data-bridge` subprojects
- Integration with JaCoCo, Checkstyle, PMD, and SpotBugs
- Quality gate configurations ready for customization

### 5. Security Best Practices

- **Never commit tokens to version control**
- **Use environment variables or Maven settings for CI/CD**
- **Regularly rotate tokens (regenerate periodically)**
- **Use project-specific tokens when possible**

### 6. Troubleshooting

If you still get authentication errors:

1. **Check token validity**: Ensure the token hasn't expired
2. **Verify server URL**: Confirm `sonar.host.url` is correct in pom.xml
3. **Check user permissions**: Ensure your user has "Execute Analysis" permission
4. **Verify project key**: Make sure `sonar.projectKey` is unique and valid
5. **File encoding**: Ensure `sonar-project.properties` is saved with UTF-8 encoding

### 7. Architecture Benefits

This hybrid configuration provides:
- **Maven compatibility**: Essential properties in pom.xml ensure plugin connectivity
- **Maintainability**: Detailed configuration externalized in properties file
- **Version control friendly**: Properties file can be easily tracked and modified
- **Multi-environment support**: Override properties via command line or profiles