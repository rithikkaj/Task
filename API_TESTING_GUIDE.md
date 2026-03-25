# API Testing Guide - TaskManager

## Prerequisites
- Backend running: `mvn clean spring-boot:run` on port 8083
- MySQL database running with `taskdb` database
- Postman or curl installed

---

## Test Flow - Step by Step

### STEP 1: Create a New Employee Account (Signup)

```
POST http://localhost:8083/auth/signup
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

**Expected Response (201):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "USER",
  "userId": 2,
  "employeeId": 2,
  "username": "johndoe",
  "email": "john@example.com"
}
```

**Save:** `token` and `employeeId` from response

---

### STEP 2: Login with Admin Account

```
POST http://localhost:8083/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "1234"
}
```

**Expected Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "ADMIN",
  "userId": 1,
  "employeeId": 1,
  "username": "admin",
  "email": "admin@example.com"
}
```

**Save:** `adminToken` for creating tasks

---

### STEP 3: Get All Employees

```
GET http://localhost:8083/employees
Authorization: Bearer {adminToken}
```

**Expected Response (200):**
```json
[
  {
    "id": 1,
    "name": "Admin User",
    "email": "admin@example.com",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "role": "ADMIN"
    }
  },
  {
    "id": 2,
    "name": "John Doe",
    "email": "john@example.com",
    "user": {
      "id": 2,
      "username": "johndoe",
      "email": "john@example.com",
      "role": "USER"
    }
  },
  {
    "id": 3,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "user": null
  }
]
```

---

### STEP 4: Create a Task (Admin assigns to John)

```
POST http://localhost:8083/tasks/create
Authorization: Bearer {adminToken}
Content-Type: application/json

{
  "title": "Complete API Documentation",
  "description": "Write comprehensive API docs for all endpoints",
  "assignedToId": 2,
  "createdById": 1,
  "dueDate": "2026-04-01"
}
```

**Expected Response (200):**
```json
{
  "id": 1,
  "title": "Complete API Documentation",
  "description": "Write comprehensive API docs for all endpoints",
  "status": "TODO",
  "dueDate": "2026-04-01",
  "assignedTo": {
    "id": 2,
    "name": "John Doe",
    "email": "john@example.com"
  },
  "createdBy": {
    "id": 1,
    "name": "Admin User",
    "email": "admin@example.com"
  }
}
```

---

### STEP 5: Create Another Task

```
POST http://localhost:8083/tasks/create
Authorization: Bearer {adminToken}
Content-Type: application/json

{
  "title": "Code Review",
  "description": "Review backend implementation",
  "assignedToId": 2,
  "createdById": 1,
  "dueDate": "2026-03-28"
}
```

---

### STEP 6: Create Task in Progress

```
POST http://localhost:8083/tasks/create
Authorization: Bearer {adminToken}
Content-Type: application/json

{
  "title": "Frontend Integration",
  "description": "Integrate API with React frontend",
  "assignedToId": 2,
  "createdById": 1,
  "dueDate": "2026-04-05"
}
```

**Then update its status:**

```
PUT http://localhost:8083/tasks/status?taskId=3&status=IN_PROGRESS
Authorization: Bearer {adminToken}
```

---

### STEP 7: Get Employee's Tasks (Using Employee Token)

```
GET http://localhost:8083/tasks/employee/2
Authorization: Bearer {userToken}
```

**Expected Response:**
```json
[
  {
    "id": 1,
    "title": "Complete API Documentation",
    "status": "TODO",
    "dueDate": "2026-04-01"
    ...
  },
  {
    "id": 2,
    "title": "Code Review",
    "status": "TODO",
    "dueDate": "2026-03-28"
    ...
  },
  {
    "id": 3,
    "title": "Frontend Integration",
    "status": "IN_PROGRESS",
    "dueDate": "2026-04-05"
    ...
  }
]
```

---

### STEP 8: Get Task Statistics ⭐ KEY ENDPOINT

```
GET http://localhost:8083/tasks/stats/employee/2
Authorization: Bearer {userToken}
```

**Expected Response (200):**
```json
{
  "totalTasks": 3,
  "completedTasks": 0,
  "inProgressTasks": 1,
  "todoTasks": 2,
  "completionPercentage": 0.0
}
```

**✅ This is what the Dashboard uses to display stats!**

---

### STEP 9: Update Task Status to Complete

```
PUT http://localhost:8083/tasks/status?taskId=1&status=DONE
Authorization: Bearer {adminToken}
```

---

### STEP 10: Check Updated Statistics

```
GET http://localhost:8083/tasks/stats/employee/2
Authorization: Bearer {userToken}
```

**Expected Response (updated):**
```json
{
  "totalTasks": 3,
  "completedTasks": 1,
  "inProgressTasks": 1,
  "todoTasks": 1,
  "completionPercentage": 33.33
}
```

---

## Postman Collection (Copy-Paste)

Save this as `TaskManager.postman_collection.json`:

```json
{
  "info": {
    "name": "TaskManager API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Signup",
          "request": {
            "method": "POST",
            "url": "http://localhost:8083/auth/signup",
            "body": {
              "raw": "{\"username\": \"testuser\", \"email\": \"test@example.com\", \"password\": \"password123\"}"
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "url": "http://localhost:8083/auth/login",
            "body": {
              "raw": "{\"username\": \"admin\", \"password\": \"1234\"}"
            }
          }
        }
      ]
    }
  ]
}
```

---

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| 401 Unauthorized | Invalid/missing token | Add `Authorization: Bearer {token}` header |
| 400 Bad Request | Invalid data format | Check JSON structure matches DTO |
| 404 Not Found | Employee/task ID doesn't exist | Verify IDs exist in database |
| 500 Server Error | Database error | Check MySQL is running, credentials correct |
| CORS Error | Frontend can't reach backend | Verify server port is 8083, CORS enabled |

---

## Verification Checklist

- [ ] Signup creates user and employee
- [ ] Login returns token with employeeId
- [ ] Tasks can be created
- [ ] Task stats endpoint returns correct counts
- [ ] Task status updates correctly
- [ ] Progress percentage calculates correctly (e.g., 1 of 3 = 33.33%)
- [ ] Dashboard loads with stats
- [ ] Frontend can fetch stats using token

---

## Browser Console Test

You can also test from browser console:

```javascript
// Test login
fetch('http://localhost:8083/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'admin', password: '1234' })
})
.then(r => r.json())
.then(data => console.log(data))

// Test get stats (replace {token} and {empId})
fetch('http://localhost:8083/tasks/stats/employee/1', {
  headers: { 'Authorization': 'Bearer {token}' }
})
.then(r => r.json())
.then(data => console.log(data))
```
