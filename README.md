# E-commerce Product Search Demo
A demonstration project showcasing advanced product search functionality with dynamic attributes using PostgreSQL and Spring Data JPA.

## Key Features
- Dynamic product attributes storage using PostgreSQL JSONB
- Flexible search query builder
- Full-text search capabilities
- Simple infrastructure setup

## Technical Implementation
- PostgreSQL as the primary database
- JSONB type for flexible product attributes
- Functional GIN index on product descriptions for optimized full-text search
- Full-text search using PostgreSQL's `websearch_to_tsquery`
- Integration with Spring Data JPA
- Native SQL queries with dynamic building
- Random product data generation via SQL migrations

## Getting Started

### Prerequisites
- Unix-like operating system
- Docker and Docker Compose
- `jq` tool (optional, for pretty-printing JSON responses)

### Installation

1. **Build the Project**
```bash
./build.sh
```

2. **Start the Application**
```bash
docker compose up -d
```

The application will be available at: `http://localhost:8080`

Database credentials:
```
URL: jdbc:postgresql://localhost:5432/ecommerce
Username: admin
Password: password
```

3. **Stop the Application**
```bash
docker compose down
```

4. **Clean Up**
```bash
./clean.sh  # Removes local docker image
```

## Usage Examples

### Filter-based Product Search
```bash
curl --location --request POST 'http://localhost:8080/products/search' \
--header 'Content-Type: application/json' \
--data-raw '{
    "categoryId": 2,
    "brand": null,
    "priceRange": null,
    "filters": {
        "processor": {
            "operator": "contains",
            "value": "AMD"
        },
        "RAM": {
            "operator": "eq",
            "value": 16
        },
        "storage_type": {
            "operator": "in",
            "values": ["NVMe"]
        }
    },
    "sort": {
        "field": "price",
        "order": "asc"
    },
    "pagination": {
        "page": 1,
        "limit": 20
    }
}' | jq '.'
```

### Full-text Product Search
```bash
curl --location --request POST 'http://localhost:8080/products/search/full-text' \
--header 'Content-Type: application/json' \
--data-raw '{
    "phrase": "Laptop Asus AMD Ryzen 7",
    "sort": {
        "field": "created_at",
        "order": "desc"
    },
    "pagination": {
        "page": 1,
        "limit": 20
    }
}' | jq '.'
```

## Future Improvements
1. Product brand normalization through separate table
2. Enhanced product descriptions for complex full-text search testing
3. Multi-language support implementation
4. Advanced full-text search using various `to_tsquery` functions
5. CRUD operations for categories and products with attribute validation
