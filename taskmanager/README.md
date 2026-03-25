# Task Manager API

## Setup
- MySQL db: taskdb, user: root, pass: ''
- Run app on localhost:8080

## Auth
- Login: POST /auth/login
  ```bash
  curl -X POST http://localhost:8080/auth/login \
    -H \"Content-Type: application/json\" \
    -d '{\"username\":\"admin\", \"password\":\"1234\"}'
  ```
  Response: {token, role}

## Tasks (use Authorization: Bearer <token>)
- Create: POST /tasks/create
- Update status: PUT /tasks/status?taskId=1&status=COMPLETED
- Get by employee: GET /tasks/employee/1
- Delete: DELETE /tasks/1?empId=1

## Fix Summary
- Fixed 400 Bad Request by implementing proper UserDetailsService & AuthenticationManager.
- Login now returns 401 on fail instead of 400.
- /tasks/** now requires JWT auth.
- Use admin/1234 to test.

Enjoy!

