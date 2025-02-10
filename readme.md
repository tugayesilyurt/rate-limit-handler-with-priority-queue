# Project Name

Building a Resilient, Priority-Driven Queue System for Rate-Limited APIs using Spring Boot, Redis and MySQL

## Requirements:

- Docker (for Redis and MySQL containers)
- Java 21 
- Maven  

## Read this article on Medium

[Medium Article](https://medium.com/@htyesilyurt/implementing-geospatial-indexing-in-spring-boot-using-redis-geohash-bd7b2b77e4d4)

# Rate-Limit Handler with Priority Queue

This project demonstrates how to build a resilient queue-driven solution for rate-limited external services using:
- **Spring Boot** for API development
- **Redis** for managing rate-limit slots and priority queues
- **MySQL** for tracking request statuses and persistence

## Key Features:

- **Priority-based Queue:** Processes requests in the order they are received, prioritizing the earliest ones.
- **Rate-Limit Management:** Dynamically handles API rate limits using Redis with configurable TTLs.
- **Scalable Design:** Easily adaptable for services with varying rate limits.

## How It Works:

1. Requests are checked for available rate-limit slots.
2. If a slot is available, the request is processed immediately.
3. Otherwise, it is added to a **priority queue** and retried periodically.
4. Requests are processed asynchronously to ensure efficiency.

## Setup:

1. Run Redis and MySQL using Docker:
   ```bash
   docker-compose up -d
