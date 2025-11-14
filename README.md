# RAG Microservices Platform — README.md

A production-ready microservices architecture for managing chat sessions & messages for a RAG-based AI platform.

## Overview

This project implements a microservices-based backend architecture designed for an AI-powered Retrieval-Augmented Generation (RAG) system.
It manages:

- User chat sessions
- Messages within each session
- System-to-system secure communication
- Centralized logging, monitoring & caching

The platform is built with Spring Boot, Spring Cloud Gateway, PostgreSQL, Redis, and a full ELK logging pipeline, all orchestrated using Docker Compose.

## Requirements
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

## Architecture Summary
- Client sends requests to API Gateway (port 8085) with X-API-KEY
- Gateway forwards requests to Chat Storage service using X-INTERNAL-KEY
- Chat Storage writes/reads from PostgreSQL and Redis
- Logstash ships logs to Elasticsearch
- Kibana provides UI dashboards

## Build Steps

Follow these steps to build and run the entire microservices platform.

### 1. Clone the repository
       git clone <your-repo-url>
       cd rag-microservices

### 2. Build all services using Maven
       mvn clean install

### 3. Start all services with Docker Compose

### Running locally
1. Create .env files (examples)
- `docker/.env.local` (develop locally)
- `docker/.env.dev` (development environment)
- `docker/.env.prod` (production environment)

2. Prepare infra - Bring the required setup via Docker - PostgreSQL, Redis, ELK, Config Server

```bash
  cd docker
  docker compose --env-file .env.local -f docker-compose.yml up --build -d
```

3. Start all services:

**Option 1: Local**:
```bash
  cd rag-chat-storag
  mvn spring-boot:run -Dspring-boot.run.profiles=local
  
  cd rag-api-gateway
  mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Option 2: via Docker**:
```bash
  docker compose --env-file .env.local up --build
```

4. To check logs of a particular service:

```bash
  docker compose --env-file .env.local logs -f rag-chat-storage
```

5. Stop all services:

```bash
   docker compose --env-file .env.local down -v --remove-orphans
   docker system prune -af --volumes
```

> Note: Ensure Docker is running and ports mentioned in the docker-compose.*.yml are available.

### Service URLs
- Chat Storage: http://localhost:8080/actuator/health
- API Gateway: http://localhost:8085/actuator/health
- Centralized Logs: **Logback → Logstash → Elasticsearch → Kibana**: http://localhost:5601/
- pgAdmin: http://localhost:5050/
- Kibana	http://localhost:5601
- Elasticsearch	http://localhost:9200
- Chat-service Swagger (internal)	http://localhost:8080/swagger-ui.html


## Tech Stack
- Java 21
- Spring Boot 3
- Spring Cloud Gateway
- PostgreSQL 15
- Redis
- ELK (Logstash/Elasticsearch/Kibana)
- Hibernate + Spring Data JPA
- Maven
- Docker + Docker Compose
- JUnit 5, Mockito

## Microservices

# API Gateway (Spring Cloud Gateway)
  - Exposes all APIs at port 8085
  - Validates X-API-KEY for all incoming requests
  - Injects X-INTERNAL-KEY when calling internal microservices
  - Publicly exposes Swagger and Actuator

# Chat Storage Service
  - Exposes internal APIs at port 8080
  - Provides session CRUD operations
  - Provides message CRUD operations with pagination
  - Uses PostgreSQL and Redis
  - Protects all /api/** endpoints with X-INTERNAL-KEY

## Features
- Microservice architecture with Spring Boot
- REST APIs for Chat Management
- API Gateway with routing and rate limiting
- PostgreSQL for reliable storage
- API Key authentication for security
- Centralized logging with ELK stack
- Health checks with Spring Boot Actuator
- Pagination support for message retrieval
- Swagger/OpenAPI documentation
- Dockerized setup for easy deployment
- Environment-specific configurations
- Unit and integration tests
- Logging of operations
- Global exception handling

## Security : 
   - Gateway enforces X-API-KEY
   - Chat-service allows only X-INTERNAL-KEY

## Observability :
   - Spring Boot Actuator
   - Elasticsearch + Logstash + Kibana


### Security (API Keys & Filters)

This project uses a two-layer API key security model to protect internal microservices while keeping documentation endpoints open.

## 1. External Access (Client → API Gateway)
All external requests to the API Gateway must include:
X-API-KEY: local-key

- Required for all `/api/**` routes
- Validated by the Gateway's `ApiKeyFilter`
- Not required for:
    - `/swagger-ui/**`
    - `/swagger-ui.html`
    - `/v3/api-docs/**`
    - `/actuator/**`

## 2. Internal Access (Gateway → Chat Storage Service)

The Gateway injects the internal key automatically when forwarding requests to the chat-service:

X-INTERNAL-KEY: <internal-service-key>

- Required for all `/api/**` endpoints within the chat-service
- Validated by the `InternalAuthFilter`
- Prevents direct access to chat-service business APIs on port 8080

## 3. Filters Used

- `ApiKeyFilter` (in API Gateway):
    - Validates incoming `X-API-KEY`
    - Injects `X-INTERNAL-KEY` for internal service communication

- `InternalAuthFilter` (in Chat Storage Service):
    - Ensures only calls with a valid `X-INTERNAL-KEY` can access internal endpoints

- Spring Security filter chains:
    - Permit Swagger UI, API docs, and Actuator endpoints
    - Protect all `/api/**` routes

## 4. Summary

- Clients → Gateway must send `X-API-KEY`
- Gateway → Chat-Service must send `X-INTERNAL-KEY`
- Swagger and Actuator endpoints require no key
- Direct access to protected endpoints on port 8080 is blocked

## Publicly allowed (no key needed)

- /swagger-ui/**
- /v3/api-docs/**
- /actuator/**

## Rate Limiting
This project implements rate limiting using Redis:

- Spring Cloud Gateway – Built-in Redis Rate Limiter
    - Location: rag-api-gateway
    - Description: Uses Spring Cloud Gateway’s built-in RedisRateLimiter to apply rate limits at the gateway layer, ensuring all incoming traffic is controlled before reaching internal microservices.


## API Documentation
- SWAGGER UI : http://localhost:8080/swagger-ui.html

## REST API Key Endpoints (rag-chat-service)
- Base path: /ragchatstorage/api/**

### Session APIs
- `POST /api/v1/sessions/` → Create new chat session
- `GET /api/v1/sessions/` → Get all chat sessions
- `GET /api/v1/sessions/{sessionId}` → Get chat session by ID
- `GET /api/v1/sessions/user/{userId}` → Get all Chat session of a particular user 
- `PATCH /api/v1/sessions/{sessionId}/rename` → Rename chat session
- `PATCH /api/v1/sessions/{sessionId}/favorite` → Mark or unmark chat session as favorite
- `DELETE /api/v1/sessions/{sessionId}` → Delete chat session by ID

- `POST /api/v1/sessions/{sessionId}/messages` → Add new message (supports optional retrieved context)
- `GET /api/v1/sessions/{sessionId}/messages?page={page}&size={size}` → Get messages for session (paginated)

> Note: Authorize and provide your API key in header X-API-Key to test endpoints.

### Testing

- Unit tests written using:
  - JUnit 5
  - Mockito

- Run tests:
  `mvn test`

## Error Handling
- Centralized GlobalExceptionHandler returns clear JSON errors with appropriate HTTP status:
    - 400 → validation errors (@Valid, @NotBlank, etc.)
    - 404 → not found (session/message ids)
    - 429 → rate-limit exceeded
    - 500 → unhandled errors (with correlation IDs in logs)

### Troubleshooting
- **Swagger on 8085 not loading**:
   Gateway does not serve chat-service swagger automatically.
  `Access directly on 8080.`

- **403 Invalid internal key**:
  Means gateway → chat-service call missing:`X-INTERNAL-KEY`

- **Chat service not reachable**:
  `Ensure chat-service is marked healthy in Docker before gateway starts.`