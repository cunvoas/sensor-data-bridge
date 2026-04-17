# SonarQube Configuration and Usage Guide

## Error Resolution

If you encountered the error:
```
Failed to execute goal org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar: 
Unable to execute SonarScanner analysis: Fail to parse entry in bootstrap index: <!doctype html>
```

This error occurs because SonarQube is trying to connect to a server that returns HTML instead of the expected API response.

## Configuration Options

### Option 1: Use SonarCloud (Recommended)

1. Sign up for a free account at [SonarCloud](https://sonarcloud.io)
2. Create an organization or join an existing one
3. Create a new project and get your project key
4. Update the configuration in `sonar-project.properties`:
   ```properties
   sonar.host.url=https://sonarcloud.io
   sonar.organization=your-actual-organization-key
   sonar.projectKey=your-actual-project-key
   ```
5. Generate a SonarCloud token from your account settings
6. Run the analysis:
   ```bash
   mvn clean verify sonar:sonar -Dsonar.token=YOUR_TOKEN
   ```

### Option 2: Use Local SonarQube Server

1. Install and start SonarQube locally:
   ```bash
   # Using Docker
   docker run -d --name sonarqube -p 9000:9000 sonarqube:latest
   
   # Or download and run SonarQube manually
   ```
2. Update the configuration:
   ```properties
   sonar.host.url=http://localhost:9000
   ```
3. Run the analysis:
   ```bash
   mvn clean verify sonar:sonar -Dsonar.login=admin -Dsonar.password=admin
   ```

### Option 3: Use Remote SonarQube Server

1. Update the configuration with your server URL:
   ```properties
   sonar.host.url=http://your-sonar-server:9000
   ```
2. Configure authentication as needed

## Running SonarQube Analysis

### Using Maven Profile
```bash
# Activate the sonar profile and run analysis
mvn clean verify -Psonar sonar:sonar -Dsonar.token=YOUR_TOKEN

# Or with username/password
mvn clean verify -Psonar sonar:sonar -Dsonar.login=YOUR_USERNAME -Dsonar.password=YOUR_PASSWORD
```

### Using sonar-project.properties
```bash
# Make sure sonar-project.properties is properly configured
mvn clean verify sonar:sonar -Dsonar.token=YOUR_TOKEN
```

## Environment Variables

You can also configure SonarQube using environment variables:
```bash
export SONAR_HOST_URL=https://sonarcloud.io
export SONAR_TOKEN=your_token
export SONAR_ORGANIZATION=your_organization
mvn clean verify sonar:sonar
```

## Troubleshooting

### Common Issues:

1. **HTML response instead of JSON**: The server URL is incorrect or pointing to a webpage
   - Solution: Verify the `sonar.host.url` points to a valid SonarQube instance

2. **Connection refused**: SonarQube server is not running
   - Solution: Start your SonarQube server or use SonarCloud

3. **Authentication failed**: Invalid credentials
   - Solution: Check your token or username/password

4. **Project not found**: Project key doesn't exist
   - Solution: Create the project in SonarQube or update the project key

### Testing Configuration

To test if your SonarQube server is accessible:
```bash
# Test SonarCloud
curl -I https://sonarcloud.io/api/system/status

# Test local SonarQube
curl -I http://localhost:9000/api/system/status
```

## Next Steps

1. Choose your SonarQube option (SonarCloud recommended for simplicity)
2. Update the configuration files with your actual values
3. Run the Maven command with proper authentication
4. Check the analysis results in the SonarQube dashboard