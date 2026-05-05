# Car Marketplace (backend)

REST API for a car listing marketplace: catalog, filters, favorites, orders, admin listings, JWT auth with refresh cookies, email verification flows.

---

## English

### Stack

- Java 21, Spring Boot 3.4, Maven  
- PostgreSQL, Spring Data JPA  
- Spring Security, JWT (JJWT)  
- AWS SDK S3-compatible storage (e.g. MinIO)  
- Spring Mail, SpringDoc OpenAPI (Swagger UI)

### Prerequisites

- JDK 21  
- PostgreSQL  
- Optional: MinIO or another S3-compatible endpoint for images  
- For HTTPS locally: a JKS keystore (or disable SSL in config for development)

### Configuration

Copy and adjust `src/main/resources/application.properties` (or use env-specific files / env vars). Never commit real secrets.

Important settings:

| Area | Properties (examples) |
|------|------------------------|
| Database | `spring.datasource.*` |
| JWT | `application.security.jwt.secret-key` (Base64-encoded key suitable for HS256), `application.security.jwt.expiration`, `application.security.jwt.refresh-token.expiration` |
| TLS | `server.ssl.*` (or set `server.ssl.enabled=false` for local HTTP) |
| Object storage | `minio.endpoint`, `minio.accesKey`, `minio.secretkey`, `minio.bucket` |

### Run

```bash
mvn spring-boot:run
```

Default port in the sample config: **8080** (with SSL if enabled).

### Tests

```bash
mvn clean test
```

Tests use an in-memory H2 database when the `test` profile is active (`src/test/resources/application-test.properties`).

### API documentation

With the app running, open Swagger UI (path depends on your SpringDoc version; typical: `/swagger-ui.html` or `/swagger-ui/index.html`).

---

## Русский

### Стек

- Java 21, Spring Boot 3.4, Maven  
- PostgreSQL, Spring Data JPA  
- Spring Security, JWT (JJWT)  
- Хранилище совместимое с S3 (например MinIO)  
- Spring Mail, SpringDoc OpenAPI (Swagger UI)

### Требования

- JDK 21  
- PostgreSQL  
- По желанию: MinIO или другой S3-совместимый сервис для картинок  
- Для HTTPS: JKS-хранилище (или отключите SSL в конфиге для разработки)

### Настройка

Отредактируйте `src/main/resources/application.properties` (или вынесите секреты в профили / переменные окружения). Реальные пароли и ключи в репозиторий не кладите.

Ключевые группы настроек:

| Область | Свойства (примеры) |
|---------|---------------------|
| БД | `spring.datasource.*` |
| JWT | `application.security.jwt.secret-key` (Base64-ключ для HS256), время жизни access/refresh |
| TLS | `server.ssl.*` или `server.ssl.enabled=false` локально |
| Файлы | `minio.*`, лимиты `spring.servlet.multipart.*` |

### Запуск

```bash
mvn spring-boot:run
```

В примере конфигурации порт **8080** (при включённом SSL — HTTPS).

### Тесты

```bash
mvn clean test
```

Для тестов поднимается H2 в памяти (профиль `test`, см. `application-test.properties`).

### Документация API

После запуска приложения откройте Swagger UI (часто `/swagger-ui.html` или `/swagger-ui/index.html`
