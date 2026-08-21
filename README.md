# SLM Suite — Service Lifecycle Management System

A role-based web application for managing spare parts inventory, dealer replenishment orders, warranty claims, and tiered pricing — inspired by real-world Service Lifecycle Management (SLM) platforms like Syncron. Built as a portfolio project to demonstrate practical Spring Boot, Spring Security, and relational data modeling skills.

---

## Overview

Aftermarket equipment manufacturers (think construction, industrial, or automotive OEMs) need to manage what happens *after* a sale: keeping spare parts stocked, letting dealers reorder parts, processing customer warranty claims, and pricing parts differently depending on who's buying. SLM Suite is a scaled-down but functionally complete version of that system, built around three connected modules and one integration story: **inventory is the shared resource that both warranty claims and dealer orders draw from.**

### Core modules

| Module | What it does |
|---|---|
| **Parts Inventory** | Full CRUD for spare parts (SKU, category, price, stock, reorder threshold, description). Includes multi-warehouse stock reference tracking and automated low-stock alerts. |
| **Orders (Dealer Replenishment)** | Dealers place orders against the parts catalog. Orders move through a real lifecycle — `PLACED → APPROVED/REJECTED → SHIPPED → DELIVERED` — with live stock deduction on approval. |
| **Warranty Claims** | Customers file claims against a part (serial number, purchase date, issue description). Claims move through `SUBMITTED → UNDER_REVIEW → APPROVED/REJECTED → PARTS_ISSUED → PAID`, deducting real inventory on approval. |
| **Pricing** | Rule-based pricing engine — price adjustments by customer tier (Retail / Dealer / Gold), with a quote calculator. |

### The integration story

This isn't three disconnected CRUD screens. **Part** is the shared entity at the center: approving a warranty claim and approving a dealer order both check and deduct from the *same* stock number, atomically, with graceful handling when stock runs out. That shared state — and the transactional integrity around it — is the core engineering problem this project solves.

---

## Roles & Access

The system supports three roles, each with a distinct login, landing page, and restricted access enforced at both the URL and UI level.

| Role | Can access | Lands on login |
|---|---|---|
| **Admin** | Parts, Warehouses, Stock, Pricing, all Orders, all Claims (approve/reject/ship/deliver/pay) | `/parts` |
| **Dealer** | Place orders, view own order history | `/orders` |
| **Customer** | File warranty claims, view own claim history | `/claims` |

Role checks happen twice: once at the Spring Security filter chain (URLs are blocked before a controller even runs), and again at the template level (action buttons are hidden from roles that shouldn't see them), so there's no path to an unauthorized action even by guessing a URL.

---

## Architecture

### Tech stack

- **Backend**: Java 17, Spring Boot 4, Spring MVC, Spring Data JPA (Hibernate)
- **Security**: Spring Security 7 — form login, BCrypt password hashing, role-based authorization, database-backed `UserDetailsService`
- **Database**: PostgreSQL, hosted on [Neon](https://neon.tech) (serverless Postgres)
- **View layer**: Thymeleaf (server-rendered), with `thymeleaf-extras-springsecurity6` for role-aware templates
- **Build tool**: Maven

### Layered design

```
Browser
   │
   ▼
SecurityFilterChain   ── role/URL checks happen first, before any controller logic runs
   │
   ▼
Controller             ── receives HTTP requests, delegates to services, picks a view
   │
   ▼
Service                ── business rules: stock checks, status transitions, @Transactional
   │
   ▼
Repository (Spring Data JPA)
   │
   ▼
PostgreSQL (Neon)
```

- **Controllers** are thin — they don't contain business logic, only routing and view selection.
- **Services** own the business rules. The most important pattern in the codebase is in `WarrantyClaimService.approveClaim()` and `OrderService.approveOrder()`: both check current stock, throw a custom `InsufficientStockException` if there isn't enough, and otherwise deduct stock and update status — all wrapped in `@Transactional` so a partial failure can never leave stock deducted without the corresponding status change (or vice versa).
- **Repositories** are plain `JpaRepository` interfaces — no hand-written SQL; Spring Data JPA generates queries from method names (e.g., `findByCustomer(User customer)`).
- **Global exception handling** (`@ControllerAdvice`) catches database constraint violations (e.g., trying to delete a Part still referenced by a claim) and generic runtime errors, and renders a friendly error page instead of a raw stack trace.

### Data model

```
User ──< Order >── Part ──< WarrantyClaim >── User
              │                    │
              └──< Stock >── Warehouse
              │
              └──< PricingRule
```

- `User` has a `Role` enum (`ADMIN`, `DEALER`, `CUSTOMER`), stored via `@Enumerated(EnumType.STRING)` for readability and refactor-safety in the database.
- `Part` is the hub: referenced by `Order`, `WarrantyClaim`, `Stock`, and `PricingRule`.
- `Order` and `WarrantyClaim` each carry their own status field acting as a lightweight state machine, with valid transitions enforced by which action buttons the UI exposes for a given current status.
- `Stock` provides a warehouse-level breakdown for reference; `Part.stockQuantity` remains the single aggregate number that order/claim approval logic actually reads and writes, to keep the transactional logic simple and centralized.

---

## Getting Started

### Prerequisites
- Java 17+
- Maven (or use the included `./mvnw` wrapper)
- A PostgreSQL database (this project was built against [Neon](https://neon.tech)'s free tier)

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/slm-suite.git
cd slm-suite
```

### 2. Configure the database
Create `src/main/resources/application.properties` (not committed to version control) with:
```properties
spring.datasource.url=jdbc:postgresql://<your-host>/<your-db>?sslmode=require
spring.datasource.username=<your-username>
spring.datasource.password=<your-password>
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.thymeleaf.cache=false
```

### 3. Run the application
```bash
./mvnw spring-boot:run
```
The app starts on `http://localhost:8080`.

### 4. Log in
Seed at least one user of each role directly in the database (see `docs/seed-data.sql` if included, or insert manually with a BCrypt-hashed password), then visit `http://localhost:8080/login`.

---

## Project Structure

```
src/main/java/com/hemanth/slmsuite/
├── entity/          # JPA entities — Part, Order, WarrantyClaim, User, Role, Warehouse, Stock, PricingRule
├── repository/       # Spring Data JPA interfaces
├── service/           # Business logic, stock/status transitions, custom exceptions
├── controller/        # Request handlers, one per module
└── config/             # SecurityConfig — auth rules, password encoding, role-based login redirect

src/main/resources/
├── templates/        # Thymeleaf views, one folder per module (parts/, orders/, claims/, pricing/, ...)
└── static/css/         # Design system stylesheet
```

---

## Known Limitations / Roadmap

This project intentionally scoped out a few things to stay focused:

- **DTOs**: forms currently bind directly to entities rather than dedicated request/response DTOs.
- **Automated tests**: no JUnit/Mockito test suite yet.
- **Schema migrations**: uses `ddl-auto=update` rather than Flyway/Liquibase versioned migrations.
- **Multi-warehouse fulfillment**: `Stock` records a warehouse-level breakdown for reference, but order/claim approval draws from the single aggregate `Part.stockQuantity` rather than a specific warehouse.
- **Demand forecasting & auto-replenishment**: not implemented.

---

## Why this project

Built to demonstrate applied understanding of layered Spring Boot architecture, relational data modeling with real foreign-key relationships, role-based access control enforced at multiple layers, and transactional integrity around shared, mutable state — the kind of problem real inventory/service systems (like Syncron's actual product suite) are built to solve.
