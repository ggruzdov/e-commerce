# Introduction
The idea behind this project was to find answers to the next questions:
- What way would be optimal to store entities like e-commerce products which have some common attributes as well as dynamic ones?
- How do we implement dynamically filtered product search? I.e. we search TVs and Washing Machines by different attributes.
- How do we implement full-text search? Say, I want to find "Gaming laptop I7".
- How do we implement pagination?

Moreover, how can we answer to all the questions and keep infrastructure simple? I.e. not to involve additional technologies 
such as MongoDB or ElasticSearch and at the same make the solution as flexible as possible(like new products addition).
And here comes plain old Postgres to the rescue.

## Key Features
- Dynamic product attributes storage
- Flexible search query builder
- Full-text search capabilities
- Pagination

## Technical Implementation
- Postgres as the primary database
- Flyway migrations
- Spring Boot 3.4.1
- Java 21
- Docker and Docker Compose
- JSONB type for flexible product attributes
- Functional GIN index on product descriptions for optimized full-text search
- Full-text search using Postgres `websearch_to_tsquery`
- Native SQL queries with dynamic building
- Random product data generation via SQL script

## Getting Started

### Prerequisites
- Unix-like operating system(for Windows just manually execute commands from shell scripts and use `mvnw.cmd` instead)
- Docker and Docker Compose
- `jq` tool (optional, for pretty-printing JSON responses in the usage examples down below)

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
6. Make filter operators as Enum