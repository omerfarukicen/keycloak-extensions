# Keycloak Extensions 

Keycloak Türkiye Sigorta uygulamalarına entegre olması amacı ile yazılmış extension kütüphanesidir.


## Setup
 jar export 
```java
mvn clean package 
```
Jar Keycloak serverına eklenir
Docker-compose 


    volumes:
      - ./keycloak-extensions.jar:/opt/keycloak/providers/keycloak-extensions.jar


Keycloak extension 

![img.png](img.png)

### ALL CONTAINER

```java
mvn clean package
```

```java
docker-compose -f docker-all.yml up
```

## Resources

- [Keycloak Rest Api Docs](https://www.keycloak.org/docs-api/22.0.1/rest-api/index.html)
- [Keycloak Configuration List](https://www.keycloak.org/server/all-config)
- [Keycloak Docs](https://www.keycloak.org/docs/22.0.1/server_admin/)
