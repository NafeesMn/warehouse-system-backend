# Warehouse Inventory System Backend

Backend API for the warehouse inventory system. This service handles authentication, inventory management, inbound and outbound stock workflows, user management, CSV product import, and PostgreSQL persistence.

Live frontend deployment: [https://warehouse-system-frontend.vercel.app/](https://warehouse-system-frontend.vercel.app/)

## Tech Stack

- Java 21
- Spring Boot 3.5
- Spring Security with JWT
- Spring Data JPA
- Flyway
- PostgreSQL
- Maven

## What This Backend Supports

- JWT-based login and authenticated profile access
- Role-based access control with `MANAGER` and `OPERATOR`
- Product create, list, detail, update, search, and CSV import
- Inbound stock receipt with stock increment
- Outbound stock shipment with insufficient-stock validation
- User administration for managers
- Dashboard summary endpoints
- Separate inbound and outbound transaction tables

## Local Setup

### Prerequisites

- Java 21
- PostgreSQL 15+ or compatible

### Database

Create a PostgreSQL database named `warehouse_system`.

### Configuration

Backend configuration is stored in [application.properties](src/main/resources/application.properties).

Defaults:

- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=warehouse_system`
- `DB_USERNAME=postgres`
- `DB_PASSWORD=postgres`
- `APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://127.0.0.1:5173,http://localhost:4173,http://127.0.0.1:4173`

You can override these with environment variables before starting the app.

### Run The Backend

```powershell
.\mvnw.cmd spring-boot:run
```

The backend runs on `http://localhost:8080`.

## Seeded Access

These users are created automatically if they do not already exist:

- Manager: `manager@artiselite.local` / `Manager@123`
- Operator: `operator@artiselite.local` / `Operator@123`

If the main tables are empty, the application also seeds demo products, suppliers, and customers for quick review.

## Product Import

Import endpoint:

- `POST /api/products/import`

Sample CSV:

- `seed-data/sample-products-import.csv`

Expected columns:

- `sku`
- `name`
- `description`
- `tags`
- `unitPrice`
- `currentStock`
- `reorderLevel`

Import behavior:

- inserts new products by SKU
- updates existing products when the SKU already exists
- validates each row independently
- returns inserted, updated, and failed counts with row-level errors

## Main API Areas

- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET|POST|PUT /api/products`
- `GET /api/products/search`
- `POST /api/products/import`
- `GET|POST /api/inbounds`
- `GET|POST /api/outbounds`
- `GET|POST|PUT|PATCH /api/users`
- `GET /api/dashboard/summary`
- `GET /api/me`

## Role Behavior

- `MANAGER` can access all modules
- `OPERATOR` can access inbound, outbound, and profile flows
- Backend authorization is the source of truth even when the frontend hides restricted navigation

## Documentation

- [Architecture](docs/architecture.md)
- [Application Flow](docs/application-flow.md)
- [ERD](docs/erd.md)

## Verification

Backend compilation has been verified with:

```powershell
.\mvnw.cmd -q -DskipTests compile
```
