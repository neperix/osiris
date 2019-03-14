# Security JWT

This module provides streamlined experience for setting up JWT based authentication with your Spring application.

## Usage

Include the security-jwt module to your dependency management tool:
```xml
<dependency>
    <groupId>com.neperix.osiris</groupId>
    <artifactId>security</artifactId>
    <version>${osiris.version}</version>
</dependency>
```
Add the following to your Spring configuration: 
```yaml
osiris:
  security:
    jwt:
      secret: your_secret
      issuer: your_organization
      timeToLiveInHours: 2

```
