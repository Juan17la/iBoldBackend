# iBold - AI Consumption Platform (Proxy Pattern)

Backend platform that simulates text generation with an AI service protected by a **proxy chain**:

1. `RateLimitProxyService` checks requests per minute by plan.
2. `QuotaProxyService` checks and deducts monthly token quota.
3. `MockAIGenerationService` simulates AI text generation (`Thread.sleep(1200ms)`).

All backend data is persisted in local JSON files (no database).

## Features

- Proxy Pattern implementation with chained proxies.
- Plans:
  - `FREE`: 10 req/min, 50,000 tokens/month.
  - `PRO`: 60 req/min, 500,000 tokens/month.
  - `ENTERPRISE`: unlimited.
- Endpoints:
  - `POST /api/auth/register`
  - `POST /api/auth/login`
  - `POST /api/ai/generate`
  - `GET /api/quota/status`
  - `GET /api/quota/history`
  - `POST /api/quota/upgrade`
- Structured error responses for frontend UX.
- Scheduled tasks:
  - Every minute: reset rate-limit windows.
  - First day of month: reset monthly quotas.

## Tech Stack

- Java 17
- Spring Boot
- Lombok
- JSON file persistence (local filesystem)

## Run Project

## Prerequisites

- Java 17+
- Maven Wrapper (included in project)

## Start

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

By default, app starts on:

- `http://localhost:8080`

## Run tests

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

## Local JSON storage

Data is stored by default in:

- `data/`

If needed, you can override the folder with the `storage.data-dir` property.

Generated files:

- `data/users.json`
- `data/quota-states.json`
- `data/rate-limits.json`
- `data/usage-history.json`

## Notes for Frontend

Detailed payload and response structures are documented in:

- `API_CONTRACT.md`
