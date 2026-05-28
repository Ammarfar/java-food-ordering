# Food Ordering API

Backend REST API for a food ordering application built with Java, Spring Boot, Spring Security, JPA, and MySQL.

## Overview

This repository contains the backend service for:

- Public product catalog
- JWT-based login
- Role-based product management
- Order checkout
- Order history for user and admin
- Swagger/OpenAPI documentation

## Tech Stack

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security + JWT
- MySQL
- H2 for tests
- Springdoc OpenAPI
- JUnit 5 + Spring Boot Test

## Project Structure

- `Controller` - REST API entry points
- `Service` - business logic
- `Repository` - database access
- `Entity` - persistence model
- `Dto` - request/response contracts
- `Security` - JWT and security config

## Database Setup

Create a MySQL database:

```sql
CREATE DATABASE food_ordering;
```

Update `src/main/resources/application.properties` if your MySQL credentials differ:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/food_ordering
spring.datasource.username=root
spring.datasource.password=admin
```

## Seed Data

Seed data is available in `src/main/resources/seed.sql`.

The seeded users use BCrypt hashes for the raw passwords:

- `user@example.com` / `user`
- `admin@example.com` / `admin`

If your environment does not auto-load the seed file, run it manually against MySQL after the schema is created.

## Run the Application

```bash
mvn spring-boot:run
```

The API will be available at:

- `http://localhost:8080`

## Swagger / API Docs

After the app starts, open:

- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui/index.html`
- `http://localhost:8080/v3/api-docs`

## Authentication

The app uses JWT authentication.

### Login

`POST /api/auth/login`

Request body:

```json
{
  "email": "admin@example.com",
  "password": "admin"
}
```

Response:

```json
{
  "message": "Login successful",
  "data": {
    "token": "jwt-token"
  }
}
```

## API Summary

### Products

- `GET /api/products?pageNo=1&pageSize=10`
- `GET /api/products/{id}`
- `POST /api/products` admin only
- `PUT /api/products/{id}` admin only
- `DELETE /api/products/{id}` admin only

### Orders

- `POST /api/orders` authenticated user
- `GET /api/orders?pageNo=1&pageSize=10` authenticated user
- `GET /api/orders/admin?pageNo=1&pageSize=10` admin only

### Response Envelope

All controller responses use the same wrapper shape:

```json
{
  "message": "Success message",
  "data": {}
}
```

For paginated endpoints, `data` contains:

```json
{
  "content": [],
  "pageNo": 1,
  "pageSize": 10,
  "totalElements": 0,
  "totalPages": 0,
  "last": true
}
```

## Pagination

- Page number is **1-based**
- Default `pageNo` is `1`
- Default `pageSize` is `10`

## Testing

### Run all tests

```bash
mvn test
```

### Run controller E2E tests only

```bash
mvn -Dtest=AuthControllerE2ETest,ProductControllerE2ETest,OrderControllerE2ETest test
```

### Test Environment

- Unit and controller tests use H2
- Test datasource config lives in `src/test/resources/application.properties`

## Notes

- Product management is restricted to admin users.
- Public product viewing does not require authentication.
- Cart is handled on the frontend as local state; backend checkout receives order items directly.
- This repository is backend-only. The frontend Angular application is not included here.
- ERD documentation is available in [ERD.md](./ERD.md).
