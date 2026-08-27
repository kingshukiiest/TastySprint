# TASTY SPRINT 🍲 — Food Ordering System

A production-style Modular Monolith food ordering platform built using **Java 21**, **Spring Boot 3.3.2**, **Spring Security 6**, **JWT**, **Spring Data JPA**, and a high-contrast minimalist Single Page Application (SPA) frontend.

---

## 🌟 Key Features

- **🔐 Stateless JWT Authentication**: Secure authentication with BCrypt password hashing, 24-hour expiration tokens, and automatic session recovery.
- **👥 Role-Based Access Control (RBAC)**: Distinct permissions for **Customers**, **Restaurant Owners**, and **Admins**.
- **🍛 Authentic Indian Menu Catalog**: Pre-seeded with items like Kolkata Chicken Biryani, Mutton Biryani, Chicken Chaap, Veg Thali, Shakes, and Paneer Butter Masala.
- **⭐ Interactive Food Reviews**: Customers can view ratings and leave star reviews (1–5 stars) with comments for every dish via a modal.
- **🛒 Shopping Cart Engine**: Automatic subtotal recalculation and orphan item cleanup (`orphanRemoval = true`).
- **📦 Order Fulfillment Lifecycle**: Complete order status tracking (`PLACED` ➔ `ACCEPTED` ➔ `PREPARING` ➔ `OUT_FOR_DELIVERY` ➔ `DELIVERED`).
- **👑 Role-Adaptive Dashboard**:
  - **Customers**: View personal order history & live order status.
  - **Restaurant Owners**: View & release order statuses for their specific restaurant.
  - **Admins**: View system-wide order history with full customer contact details.

---

## 🛠️ Tech Stack

- **Backend**: Java 21, Spring Boot 3.3.2, Spring Security 6, JJWT 0.12.6, Spring Data JPA, Jakarta Validation, Lombok.
- **Database**: MySQL 8 (Production) / H2 In-Memory (Zero-config testing).
- **Frontend**: Lightweight SPA (HTML5, Vanilla CSS3 tokens, Async/Await JavaScript).

---

## 🏗️ Architecture & Request Mapping

The application follows a clean 3-tier Modular Monolith structure:

```text
HTTP Request ➔ JwtAuthenticationFilter ➔ @RestController ➔ Service Layer ➔ Spring Data JPA Repository ➔ Database
```

### Flow Example: Adding Item to Cart (`POST /api/cart/items`)
1. **Security Filter**: `JwtAuthenticationFilter` validates `Authorization: Bearer <token>`, decodes email, and populates `SecurityContextHolder`.
2. **Controller**: `CartController` validates request payload using Jakarta `@Valid`.
3. **Service**: `CartServiceImpl` verifies `Role.CUSTOMER`, lazily initializes cart entity if needed, updates item quantity, recalculates total, and saves.
4. **Repository**: `CartRepository` generates SQL statements (`INSERT INTO cart_items ...`).
5. **Response**: Wraps payload in standardized `ApiResponse<CartResponse>` JSON envelope.

---

## ⚡ REST API Endpoints Overview

| Category | Method | Endpoint | Access Rule | Description |
| :--- | :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/register` | Public | Register new Customer, Owner, or Admin |
| **Auth** | `POST` | `/api/auth/login` | Public | Authenticate user & receive JWT token |
| **Foods** | `GET` | `/api/foods` | Public | Fetch all menu catalog items |
| **Foods** | `GET` | `/api/foods/search?name={name}` | Public | Search food items by partial name |
| **Cart** | `GET` | `/api/cart` | Customer | Fetch current customer shopping cart |
| **Cart** | `POST` | `/api/cart/items` | Customer | Add food item to cart |
| **Orders** | `POST` | `/api/orders` | Customer | Checkout cart and place order |
| **Orders** | `GET` | `/api/orders` | Authenticated | View orders (Role-scoped results) |
| **Orders** | `PUT` | `/api/orders/{id}/status` | Owner / Admin | Release & update order fulfillment state |
| **Reviews**| `GET` | `/api/foods/{id}/reviews` | Public | Fetch reviews for a specific food item |
| **Reviews**| `POST` | `/api/reviews` | Authenticated | Post star rating (1–5) and comment |

---

## 🔑 How Security Works

- **Stateless Sessions**: Sessions are configured as `SessionCreationPolicy.STATELESS` in `SecurityConfig.java`.
- **JWT Filter**: `JwtAuthenticationFilter` extends `OncePerRequestFilter` to intercept Bearer tokens before reaching controllers.
- **Method-Level Protection**: Annotations like `@PreAuthorize("hasAnyAuthority('RESTAURANT_OWNER', 'ADMIN')")` guard management endpoints.
- **Custom JSON Error Responses**: Unauthenticated or forbidden attempts trigger custom `AuthenticationEntryPoint` and `AccessDeniedHandler` JSON responses.

---

## 🚀 Step-by-Step Setup & Running

### Prerequisites
- **JDK 21** installed
- **Maven 3.x** installed
- **MySQL 8.x** (Optional — H2 in-memory mode works out of the box)

### Option 1: Quick Run (Zero-Config H2 In-Memory)
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/tasty-sprint.git
   cd tasty-sprint
   ```
2. In `src/main/resources/application.properties`, ensure H2 datasource is enabled.
3. Build and run:
   ```bash
   mvn spring-boot:run
   ```
4. Open browser at **[http://localhost:8080/](http://localhost:8080/)**.

### Option 2: Run with MySQL
1. Create the database in MySQL:
   ```sql
   CREATE DATABASE food_ordering_db;
   ```
2. Update `application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/food_ordering_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

---

## 🧪 Demo Login Credentials

The application automatically seeds demo accounts on startup:

| Role | Email | Password | What You Can Do |
| :--- | :--- | :--- | :--- |
| **Customer** | `customer@tastysprint.com` | `password123` | Order food, manage cart, post reviews |
| **Owner** | `owner@tastysprint.com` | `password123` | View restaurant orders, update release status |
| **Admin** | `admin@tastysprint.com` | `password123` | Full system overview of all orders & users |
