
# Finance Dashboard API

A Spring Boot-based Financial Management System that allows users to manage income, expenses, and visualize financial insights with secure role-based access.


## Features

- JWT-based Authentication & Authorization
- User Profile Management (View, Update, Delete)
- Financial Records (Income/Expense tracking)
- Dashboard Analytics
- Monthly Trends
- Category-wise Expense Breakdown
- Filtering + Pagination APIs
- Role-based Access Control (ADMIN, ANALYST, VIEWER)


## Architecture Overview


![Architecture Diagram](https://cdn.phototourl.com/free/2026-04-04-72288a64-bf45-4229-af12-217c2acc95a2.png)


## Project Structure

- controller/   → Handles API requests
- service/      → Business logic
- repository/   → DB interaction (JPA)
- model/        → Entities (DB tables)
- dto/          → Request/Response objects
- security/     → JWT & authentication
- exception/    → Global exception handling
- config/       → App configurations


## Tech Stack

- **Backend:** Spring Boot 4, Spring Security, Spring Data JPA
- **Database**: MySQL
- **Security**: JWT + Role-based Authorization
- **Documentation**: OpenAPI / Swagger
- **Build Tool**: Maven


## API Reference

### Auth Controller

#### Request Body

```js
request("POST /api/auth/register, {
 {
  "name": "Akshay",
  "email": "akshay@gmail.com",
  "password": "123456",
  "role": "ROLE_ANALYST"
}
```

#### Response Body 
```js 
{
  "token": "jwt-token-here",
  "email": "akshay@gmail.com",
  "role": "ROLE_ANALYST"
}
```

### LOGIN

#### Request Body

```http
  POST /api/auth/login
```

#### Response Body

```js
{
  "email": "akshay@gmail.com",
  "password": "123456"
}
```
#### Response Body

```js 
{
  "token": "jwt-token-here",
  "name":  "yourname",
  "message": "Login successful"
  "email": "your@email.com",
  "type":  "Bearer",
  "userId": 1
 }
```

### User Controller

#### Get All Users (ADMIN only)
```http
GET /api/users
```

#### header
```http
Authorization: Bearer <JWT_TOKEN>
```

#### Get Current User Profile
```http
GET /api/users/me
```
#### Response
```js
{
  "id": 1,
  "name": "Akshay",
  "email": "akshay@gmail.com",
  "role": "ROLE_ANALYST",
  "status": "ACTIVE"
}
```
### Financial Records Controller
#### Create Record
```http
POST /api/records
```

#### Request Body
```js
{
  "amount": 5000,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "description": "Monthly salary"
}
```
#### Get All Records
```http
GET /api/records
```
#### Delete Record (ADMIN only)
```http
DELETE /api/records/{id}
```

### Dashboard APIs
 #### Summary
```http
GET /api/records/summary
```

#### Response
```http
{
  "totalIncome": 10000,
  "totalExpense": 4000,
  "netBalance": 6000
}
```
#### Monthly Trends
```http
GET /api/records/monthly-trends
```

#### Category-wise Expenses
```http
GET /api/records/category-wise
```
#### Full Dashboard
```http
GET /api/records/dashboard
```
#### Filter + Pagination API
```http
GET /api/records/filter
```



## DB design 

![Architecture Diagram](https://cdn.phototourl.com/free/2026-04-04-3773f466-55a0-4aa4-b8aa-5dd7e408d253.png)
