# Project Name

Building a Resilient, Priority-Driven Queue System for Rate-Limited APIs using Spring Boot, Redis and MySQL

## Requirements:

- Docker (for Redis and MySQL containers)
- Java 21 
- Maven  

## Read this article on Medium

[Medium Article](https://medium.com/@htyesilyurt/building-a-resilient-priority-driven-queue-system-for-rate-limited-apis-using-spring-boot-redis-0729d1e56c36)

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

1. Run Redis using Docker:
   ```bash
   docker run -d --name redis-container -p 6379:6379 --restart unless-stopped redis:latest
   ```
2. Run MySQL using Docker:
   ```bash
   docker run -d --name mysql-container -p 3306:3306 -e MYSQL_ROOT_PASSWORD=rootpassword -e MYSQL_DATABASE=queue -e MYSQL_USER=queue -e MYSQL_PASSWORD=123456 --restart unless-stopped mysql:latest
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```