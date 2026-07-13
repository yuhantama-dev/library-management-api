# 📚 Library Management API

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen)
![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791)
![JWT](https://img.shields.io/badge/Security-JWT-orange)
![Lombok](https://img.shields.io/badge/Lombok-1.18.46-red)
![License](https://img.shields.io/badge/License-MIT-yellow)

> **A production‑grade REST API for managing a book library.**  
> Built with Spring Boot 3, JWT authentication, role‑based access control, and full CRUD with pagination.

---

## ✨ Features

- **🔐 JWT Authentication** – Users can register, log in, and receive a token.
- **👥 Role‑Based Access** – `ADMIN` can manage books and categories; `USER` can view and review books.
- **📖 Book Management** – Full CRUD for books (title, author, ISBN, publication year, category).
- **📝 Reviews** – Users can post, update, and delete their own reviews; admins can delete any review.
- **🗂️ Categories** – Books are organised into categories (Fiction, Non‑Fiction, Science, History, Fantasy).
- **📄 Pagination & Sorting** – List endpoints support `page`, `size`, and `sort` parameters.
- **✅ Global Exception Handling** – Consistent JSON error responses with validation details.
- **🌱 Sample Data Seeder** – Pre‑populates categories, books, users, and reviews for quick testing.

---

## 🛠️ Tech Stack

| Layer          | Technology                                                                 |
| :------------- | :------------------------------------------------------------------------- |
| **Language**   | Java 21 (LTS)                                                              |
| **Framework**  | Spring Boot 3.5.16                                                         |
| **Security**   | Spring Security 6 + JJWT (JSON Web Tokens)                                 |
| **Database**   | PostgreSQL (or H2 for development)                                        |
| **ORM**        | Spring Data JPA / Hibernate                                                |
| **Build Tool** | Apache Maven                                                               |
| **Utilities**  | Lombok, Jakarta Bean Validation                                            |

---

## 🚀 Getting Started (Run Locally)

### Prerequisites
- Java 21
- Maven (or use the included Maven wrapper)
- PostgreSQL (or Docker) – *or* use H2 in‑memory for quick setup

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yuhantama-dev/library-management-api.git
   cd library-management-api
   ```

2. **Configure the database**
   - If using **PostgreSQL**, create a database and update `application.properties`:
     ```properties
     spring.datasource.url=jdbc:postgresql://localhost:5432/librarydb
     spring.datasource.username=library
     spring.datasource.password=library123
     ```
   - If you prefer **H2** (in‑memory), add the H2 dependency and change the URL accordingly (check the code for details).

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```
   The API will start at `http://localhost:8080`.

4. **Seed data** (optional but useful)
   - The application automatically seeds roles, an admin user, 5 categories, 20 books, 5 regular users, and random reviews.
   - Admin login: `admin@library.com` / `admin123`
   - Sample user: `alice@example.com` / `password123`

5. **Test with curl or Postman**

---

## 📡 API Endpoints

All endpoints are prefixed with `/api`.  
Authentication is required for most endpoints (except `/auth/**`).  
Use the `Authorization: Bearer <token>` header.

### Authentication

| Method | Endpoint               | Description                        |
| :----- | :--------------------- | :--------------------------------- |
| POST   | `/api/auth/register`   | Register a new user (returns user) |
| POST   | `/api/auth/login`      | Login, returns JWT token          |

**Register example:**
```json
POST /api/auth/register
{
    "name": "John",
    "email": "john@example.com",
    "password": "secret123"
}
```

**Login example:**
```json
POST /api/auth/login
{
    "email": "admin@library.com",
    "password": "admin123"
}
// Response:
{
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "email": "admin@library.com",
    "role": "USER"
}
```

---

### Books (`/api/books`)

| Method | Endpoint               | Description                     | Access        |
| :----- | :--------------------- | :------------------------------ | :------------ |
| GET    | `/api/books`           | Get all books (paginated)       | Authenticated |
| GET    | `/api/books/{id}`      | Get a single book by ID         | Authenticated |
| POST   | `/api/books`           | Create a new book               | `ADMIN`       |
| PUT    | `/api/books/{id}`      | Update a book                   | `ADMIN`       |
| DELETE | `/api/books/{id}`      | Delete a book                   | `ADMIN`       |

**Query parameters for GET all books:**
- `page` (default: 0)
- `size` (default: 10)
- `sort` (default: `id,asc` – e.g., `title,desc`)

**Book request/response example:**
```json
{
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "isbn": "9780547928227",
    "publicationYear": 1937,
    "categoryId": 5
}
// Response includes category name, createdAt, and id
```

---

### Reviews (`/api/reviews`)

| Method | Endpoint                     | Description                              | Access        |
| :----- | :--------------------------- | :--------------------------------------- | :------------ |
| POST   | `/api/reviews`               | Create a review for a book               | Authenticated |
| GET    | `/api/reviews/book/{bookId}` | Get all reviews for a book (paginated)   | Authenticated |
| GET    | `/api/reviews/my-reviews`    | Get reviews by the current user          | Authenticated |
| PUT    | `/api/reviews/{reviewId}`    | Update a review (owner or admin)         | Authenticated |
| DELETE | `/api/reviews/{reviewId}`    | Delete a review (owner or admin)         | Authenticated |

**Review request:**
```json
{
    "rating": 5,
    "comment": "Fantastic book!",
    "bookId": 1
}
```

---

### Admin Dashboard

| Method | Endpoint                 | Description               | Access  |
| :----- | :----------------------- | :------------------------ | :------ |
| GET    | `/api/admin/dashboard`   | Simple admin test endpoint | `ADMIN` |

---

## 📁 Project Structure

```
src/main/java/com/yourname/library/
├── LibraryManagementApplication.java      # Main entry point
├── config/
│   └── DataLoader.java                    # Seeds sample data on startup
├── controller/
│   ├── AuthController.java
│   ├── BookController.java
│   ├── ReviewController.java
│   └── AdminController.java
├── dto/
│   ├── auth/                              # Login/Register DTOs
│   ├── BookRequestDTO.java
│   ├── BookResponseDTO.java
│   ├── ReviewRequestDTO.java
│   ├── ReviewResponseDTO.java
│   ├── PageResponse.java                  # Generic pagination wrapper
│   └── ErrorResponse.java                 # Standard error format
├── entity/
│   ├── User.java
│   ├── Role.java
│   ├── Book.java
│   ├── Category.java
│   └── Review.java
├── repository/
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── BookRepository.java
│   ├── CategoryRepository.java
│   └── ReviewRepository.java
├── service/
│   ├── AuthService.java
│   ├── BookService.java
│   ├── ReviewService.java
│   └── CustomUserDetailsService.java
├── security/
│   ├── JwtUtil.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

---

## 🗂️ Atomic Git Commit History

This project follows a clean, atomic commit strategy. Each commit represents one fully working feature:

- `init: project skeleton with dependencies`
- `feat: configure PostgreSQL and JPA`
- `feat: implement JWT authentication`
- `feat: add Role entity and ManyToMany`
- `feat: create Book and Category entities`
- `feat: implement Book CRUD with security`
- `feat: add Review entity and relationships`
- `feat: implement Review endpoints`
- `feat: add pagination and sorting`
- `feat: global exception handling & validation`
- `docs: final README`

---

## 📚 What I Learned

- How to secure a REST API with **Spring Security and JWT**.
- Working with **JPA relationships** (`@ManyToMany`, `@OneToMany`, `@ManyToOne`).
- Implementing **role‑based access control** with `@PreAuthorize`.
- Using **PostgreSQL** in a Spring Boot application.
- Adding **pagination and sorting** with Spring Data `Pageable`.
- Creating a **global exception handler** to standardise error responses.
- Writing an **idempotent data seeder** for testing and development.

---

## 🚧 Future Improvements

- Add **unit and integration tests** (JUnit 5 + Mockito).
- Implement **book search** by title or author.
- Add **caching** (e.g., Redis) to improve performance.
- Expose a **Swagger / OpenAPI** documentation endpoint.
- Deploy to a cloud platform (AWS, Heroku, etc.).
- Add **email confirmation** for user registration.

---

## 🤝 Connect with Me

[![Website](https://img.shields.io/badge/Website-yuhananda.dev-4285F4?style=social&logo=google-chrome)](https://yuhananda.dev)
[![GitHub](https://img.shields.io/badge/GitHub-yuhantama--dev-181717?style=social&logo=github)](https://github.com/yuhantama-dev)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-yuhanadit-0A66C2?style=social&logo=linkedin)](https://linkedin.com/in/yuhanadit)

---

## 📄 License

This project is open‑source and available under the [MIT License](LICENSE).