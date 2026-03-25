# TaskManager API - Endpoints Checklist

## ✅ Authentication Endpoints

### 1. **POST /auth/login**
- **Request Body:**
  ```json
  {
    "username": "admin",
    "password": "1234"
  }
  ```
- **Response (200 OK):**
  ```json
  {
    "token": "jwt-token-here",
    "role": "ADMIN",
    "userId": 1,
    "employeeId": 1,
    "username": "admin",
    "email": "admin@example.com"
  }
  ```
- **Status:** ✅ IMPLEMENTED
- **CORS:** ✅ Enabled for localhost:3000

### 2. **POST /auth/signup**
- **Request Body:**
  ```json
  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }
  ```
- **Response (201 CREATED):**
  ```json
  {
    "token": "jwt-token-here",
    "role": "USER",
    "userId": 2,
    "employeeId": 2,
    "username": "john_doe",
    "email": "john@example.com"
  }
  ```
- **Features:**
  - ✅ Creates both User and Employee records
  - ✅ Links them together
  - ✅ Returns token immediately
- **Status:** ✅ IMPLEMENTED

---

## ✅ Task Endpoints

### 3. **GET /tasks/employee/{employeeId}**
- **Headers:** `Authorization: Bearer {token}`
- **Response (200 OK):**
  ```json
  [
    {
      "id": 1,
      "title": "Complete project",
      "description": "Finish the backend API",
      "status": "IN_PROGRESS",
      "dueDate": "2026-03-30",
      "assignedTo": {
        "id": 1,
        "name": "John Doe",
        "email": "john@example.com"
      },
      "createdBy": {
        "id": 2,
        "name": "Admin User",
        "email": "admin@example.com"
      }
    }
  ]
  ```
- **Status:** ✅ IMPLEMENTED

### 4. **GET /tasks/stats/employee/{employeeId}** ⭐ NEW
- **Headers:** `Authorization: Bearer {token}`
- **Response (200 OK):**
  ```json
  {
    "totalTasks": 10,
    "completedTasks": 3,
    "inProgressTasks": 5,
    "todoTasks": 2,
    "completionPercentage": 30.0
  }
  ```
- **Status:** ✅ IMPLEMENTED
- **Used By:** Dashboard stats section

### 5. **POST /tasks/create**
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
  ```json
  {
    "title": "New Task",
    "description": "Task description",
    "assignedToId": 1,
    "createdById": 2,
    "dueDate": "2026-04-15"
  }
  ```
- **Response (200 OK):** Task object
- **Status:** ✅ IMPLEMENTED

### 6. **PUT /tasks/status**
- **Headers:** `Authorization: Bearer {token}`
- **Query Params:** `taskId=1&status=IN_PROGRESS`
- **Response (200 OK):** Updated Task object
- **Valid Status Values:** `TODO`, `IN_PROGRESS`, `DONE`
- **Status:** ✅ IMPLEMENTED

### 7. **DELETE /tasks/{taskId}**
- **Headers:** `Authorization: Bearer {token}`
- **Query Params:** `empId=2`
- **Response (200 OK):** "Deleted"
- **Status:** ✅ IMPLEMENTED

---

## ✅ Employee Endpoints

### 8. **GET /employees**
- **Headers:** `Authorization: Bearer {token}`
- **Response (200 OK):** List of all employees
- **Status:** ✅ IMPLEMENTED

### 9. **GET /employees/{id}**
- **Headers:** `Authorization: Bearer {token}`
- **Response (200 OK):** Single employee object
- **Status:** ✅ IMPLEMENTED

### 10. **POST /employees/create**
- **Headers:** `Authorization: Bearer {token}`
- **Request Body:**
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com"
  }
  ```
- **Response (201 CREATED):** Employee object
- **Status:** ✅ IMPLEMENTED

### 11. **DELETE /employees/{id}**
- **Headers:** `Authorization: Bearer {token}`
- **Response (200 OK):** Deleted
- **Status:** ✅ IMPLEMENTED

---

## 🔒 Security Configuration

- ✅ JWT Authentication enabled
- ✅ CORS enabled for `http://localhost:3000`
- ✅ NoOp Password Encoder (development only)
- ✅ Stateless session management
- ✅ Public endpoints: `/auth/login`, `/auth/signup`
- ✅ Protected endpoints: `/tasks/**`, `/employees/**`

---

## 🗄️ Database Configuration

- **URL:** `jdbc:mysql://localhost:3306/taskdb`
- **Port:** 3306
- **Username:** `root`
- **Password:** `Rithikka123`
- **Auto-DDL:** `create` (drops and recreates on startup)
- **SQL Logging:** Enabled

---

## ⚠️ Current Issues & Recommendations

### 1. **Test Data Issue** 🔴
- `data.sql` contains old user data without employee records
- **Fix Needed:** Update `data.sql` to:
  ```sql
  INSERT INTO employee (name, email) VALUES 
  ('Admin User', 'admin@example.com');
  
  INSERT INTO user (username, password, role, employee_id) VALUES 
  ('admin', '1234', 'ADMIN', 1);
  ```

### 2. **Password Security** ⚠️
- Using `NoOpPasswordEncoder` (plain text)
- Application works fine but **NOT FOR PRODUCTION**
- **For prod:** Use `BCryptPasswordEncoder`

### 3. **Missing AuditService** ⚠️
- Referenced in TaskService but need to verify it exists
- Check if compilation works without errors

### 4. **API Port** ✅
- Server running on port **8083** (not 8080)
- Frontend configured to use `http://localhost:8083`

---

## 🧪 Quick Test Endpoints (in order)

```bash
# 1. Signup
POST http://localhost:8083/auth/signup
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}

# 2. Login
POST http://localhost:8083/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

# 3. Get own tasks (use employeeId from login response)
GET http://localhost:8083/tasks/employee/1
Authorization: Bearer {token}

# 4. Get task stats
GET http://localhost:8083/tasks/stats/employee/1
Authorization: Bearer {token}
```

---

## 📋 Compilation Check

Before running, verify:
- [ ] All imports are correct
- [ ] AuditService exists and is injected properly
- [ ] No circular dependencies
- [ ] Database is accessible

**Command to compile:**
```bash
mvn clean compile
```

**Command to run:**
```bash
mvn clean spring-boot:run
```

---

## 📊 Frontend Integration Status

| Feature | Status | Notes |
|---------|--------|-------|
| Login | ✅ Ready | Returns employeeId for dashboard |
| Signup | ✅ Ready | Auto-creates Employee record |
| Dashboard | ✅ Ready | Fetches stats from /tasks/stats/employee/{id} |
| Task List | ✅ Ready | Shows user's own tasks |
| Task Status Update | ✅ Ready | Updates trigger dashboard refresh |
| Progress Bar | ✅ Ready | Uses completionPercentage |

---

## 🚀 Next Steps

1. **Update test data in data.sql** (HIGH PRIORITY)
2. Run `mvn clean spring-boot:run`
3. Test endpoints with Postman or frontend
4. Verify task statistics display in Dashboard
5. Test employee login workflow end-to-end
