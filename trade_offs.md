# Trade-offs

## Context

- Scalability is not a primary concern yet, but future growth should be considered
- Monolith architecture (No immediate distributed-system requirements)
- Small engineering team
- Database: MySQL
- No heavy batch-write requirements
- No offline workflows

---

## Decision #1: Use `IDENTITY (Long)` instead of `UUID` as Primary Key

### Why

- Smaller PK/FK size → Cheaper joins and indexes → better DB performance
- MySQL-native optimization (Uses `AUTO_INCREMENT`)
- Batch inserts are not a requirement (Hibernate `IDENTITY` batching limitations are acceptable)
- Easier debugging and log tracing

### Trade-offs

- ID only exists after DB persistence:
  - Harder to support offline-first workflows
  - Adds DB dependency in event-driven systems

### Mitigation

- Add `publicId` for external-facing identity:
  - `id` → joins, FK, internal DB operations
  - `publicId` → APIs, events, future distributed communication

```java
@Column(unique = true, nullable = false)
private UUID publicId;
```

---

## Decision #2: Avoid Lombok `@Data`, `@AllArgsConstructor`, `@Builder`, and unrestricted constructors in Entities

### Why

- Entities should control how they are created and modified to make sure data validated.
- Keep business rules inside the domain model
- Easier unit testing
- Prevent accidental mutation
- JPA entities often require controlled construction.

### Trade-offs

- More boilerplate code (slower development speed)
  - Explicit constructors
  - Explicit getters
  - Domain methods

### Mitigation

Use Lombok selectively:

Allowed:

```java
@Getter
@NoArgsConstructor(access = PROTECTED)
```

Avoid:

```java
@Data
@AllArgsConstructor
@Builder
```

for domain entities.