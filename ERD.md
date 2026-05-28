# ERD

```mermaid
erDiagram
    USERS {
        bigint id PK
        varchar name
        varchar email UK
        varchar password
        enum role
    }

    PRODUCTS {
        bigint id PK
        varchar name
        text description
        decimal price
    }

    ORDERS {
        bigint id PK
        bigint user_id FK
        enum status
    }

    ORDER_ITEMS {
        bigint id PK
        bigint order_id FK
        int quantity
        decimal price_snapshot
        varchar name_snapshot
    }
```

## Relationships

- `users` 1 to many `orders`
- `orders` 1 to many `order_items`
- `order_items` stores a snapshot of product name and price at checkout time
- `products` is referenced during checkout but is not directly linked from `order_items`

## Notes

- Cart is a frontend-only local state and does not exist in the backend database.
- Order snapshots are persisted so historical orders remain unchanged if product data changes later.
