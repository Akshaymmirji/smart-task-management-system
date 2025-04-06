# 🧠 Smart Task Management System

A robust and scalable task management web application designed to help teams and individuals manage tasks efficiently. Built with **Spring Boot**, **MySQL**, and **Spring Security**, it supports role-based access, real-time notifications, and detailed task analytics.

---

## 📌 Features

- ✅ **User Authentication** with role-based access (Admin/User)
- 📝 **Task Management**: Create, update, delete, assign, and filter tasks
- ⏰ **Reminders & Deadlines**: Set due dates and task priorities
- 📬 **Notifications**: Email alerts on task creation/updates
- 🔍 **Search & Filter**: By title, description, status, priority, deadline
- 📊 **Task Analytics** (admin-only)
- 🛠️ **Logging & Error Handling**
- ✅ **Unit Testing** using JUnit & Mockito

---

## 🧱 Tech Stack

| Layer           | Technology                          |
|----------------|-------------------------------------|
| Language        | Java                                |
| Framework       | Spring Boot 3.2.0                   |
| Build Tool      | Maven                               |
| Security        | Spring Security with RBAC           |
| Database        | MySQL 8.x                           |
| API Testing     | Postman                             |
| Testing         | JUnit 5, Mockito                    |
| IDE             | Eclipse / IntelliJ IDEA             |

---

## 🧑‍💻 Roles and Permissions

| Feature                         | Admin ✅ | User ✅ |
|----------------------------------|----------|----------|
| User Authentication              | ✅        | ✅        |
| Manage Tasks (CRUD)              | ✅ (all)  | ✅ (own)  |
| Assign Tasks                     | ✅        | ❌        |
| Set Deadlines & Priorities       | ✅ (all)  | ✅ (own)  |
| Search & Filter Tasks            | ✅ (all)  | ✅ (own)  |
| View Task Analytics              | ✅        | ❌        |
| Notifications                   | ✅ (all)  | ✅ (own)  |
| Manage Users                     | ✅        | ❌        |
| Logging & Error Handling         | ✅        | ❌        |

---

## 📁 Project Structure

```
SmartTaskManagementSystem/
├── src/
│   ├── main/
│   │   ├── java/com/smarttask/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── config/
│   │   │   └── security/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   └── test/java/com/smarttask/
│       ├── controller/
│       └── service/
├── pom.xml
└── README.md
```

---

## 🔌 API Endpoints

### 🔐 Authentication

| Method | Endpoint           | Description        |
|--------|--------------------|--------------------|
| POST   | `/auth/register`   | Register user      |
| POST   | `/auth/login`      | Login and get JWT  |

### 📋 Task Management

| Method | Endpoint                         | Description                        |
|--------|----------------------------------|------------------------------------|
| POST   | `/tasks`                         | Create new task                    |
| GET    | `/tasks`                         | Get all tasks                      |
| GET    | `/tasks/{id}`                    | Get task by ID                     |
| PUT    | `/tasks/{id}`                    | Update task                        |
| DELETE | `/tasks/{id}`                    | Delete task                        |

### 🔍 Search & Filter

| Method | Endpoint                                         | Description                            |
|--------|--------------------------------------------------|----------------------------------------|
| GET    | `/tasks/search?query={text}`                     | Search by title/description            |
| GET    | `/tasks/filter/status?status=PENDING`            | Filter by status                       |
| GET    | `/tasks/filter/priority?priority=HIGH`           | Filter by priority                     |
| GET    | `/tasks/filter/deadline?from=YYYY-MM-DD&to=...`  | Filter by deadline range               |

### 📩 Notifications

| Method | Endpoint                       | Description                     |
|--------|--------------------------------|---------------------------------|
| GET    | `/notifications/user/{userId}` | Get notifications for a user    |

---

## 🗄️ Database Schema (MySQL)

**Tables:**
- `users` — Stores user details and roles
- `tasks` — Task information and metadata
- `notifications` — Task-related notifications

---

## 🧪 Testing

- Unit and integration testing with **JUnit 5** and **Mockito**
- Controller and service layer tests for `UserService`, `TaskService`, etc.
- Run tests using:
```bash
mvn test
```

---

## 📦 How to Run

1. Clone the repository  
   `git clone https://github.com/your-username/smart-task-management-system.git`

2. Import into Eclipse or IntelliJ as a Maven project

3. Configure `application.properties` with your MySQL credentials

4. Run the project using:
```bash
mvn spring-boot:run
```

5. Test endpoints with Postman

---

## 📈 Future Scope

- Add frontend (React/Angular)
- Real-time notifications with WebSockets
- Role-based dashboard analytics
- SMS/email scheduler integrations

---

## 🤝 Contributing

Contributions are welcome! Fork the repo and create a pull request for review.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---