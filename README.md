# 🧠 AI Learning Tracker

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED)
![Gemini AI](https://img.shields.io/badge/AI-Google_Gemini-8E75B2)

**AI Learning Tracker** is a smart self-reflection tool designed to help users achieve their learning goals through AI-driven planning and analysis.

Unlike standard to-do lists, this application focuses on the **quality** of learning. The user sets a goal, the AI breaks it down into a structured plan, and then analyzes the user's progress logs to provide personalized recommendations for improvement.

The project follows a **Client-Server architecture** with a decoupled frontend and is fully containerized using Docker.

## Key Features

### 1. AI-Powered Goal Planning
* **Smart Decomposition:** The user simply inputs a high-level goal (e.g., "Learn Java").
* **Auto-Generation:** Google Gemini AI analyzes the input and generates a comprehensive study plan with detailed subtasks and additional resources.

### 2. Learning Journal & Reflection
* **Progress Logging:** Users can log their daily activities, thoughts, and challenges for each specific goal.
* **Self-Reflection:** The system encourages users to reflect on *how* they are learning, not just *what* they finished.

### 3. AI Insights & Recommendations
* **Log Analysis:** The AI reviews the user's logs to identify patterns, bottlenecks, or effective strategies.
* **Actionable Feedback:** Users receive tailored advice on how to improve their learning efficiency based on their actual history.

### 4. Advanced Security
* **Flexible Auth:** Sign up via Email/Password or **Google OAuth2**.
* **Two-Factor Authentication (2FA):** Optional extra layer of security using Google Authenticator (TOTP), manageable via settings.

---

## Tech Stack

**Backend:**
* **Core:** Java 21, Spring Boot 3
* **Security:** Spring Security, OAuth2 Client, JWT, 2FA (Access/Refresh Tokens)
* **Database:** PostgreSQL, Liquibase (Migrations)
* **AI Integration:** Google Gemini API

**Frontend:**
* **Stack:** Vanilla JS, HTML5, CSS3
* **Server:** Nginx (Reverse Proxy & Static File Serving)

**DevOps:**
* **Containerization:** Docker & Docker Compose

---

## Getting Started

You can run the application using Docker or set it up locally.

### Prerequisites (Environment Config)

Before running the app, you need to configure the environment variables.
Create a `.env` file in the root directory by copying the template:

```bash
  cp .env.example .env
```

### Option 1: Run via Docker 

This command spins up the Database, Backend, and Frontend containers automatically.

Make sure Docker Desktop is running.

Run the command:

```bash
  docker-compose up --build
```
**Access the application:**

**Frontend:** http://localhost:3000

**Backend API:** http://localhost:8080

### Option 2: Local Development

If you prefer to run the code via an IDE (e.g., IntelliJ IDEA) without full containerization.

**1. Backend (Java):**

Open the project in IntelliJ IDEA.

Configure Environment Variables in your Run Configuration (copy from .env, but change DATASOURCE_URL to jdbc:postgresql://localhost:5432/...).

Run AiLearningTrackerApplication.

**2. Frontend:**

Since the frontend is built with Vanilla JS, you can serve frontend/index.html using any local server (e.g., Live Server).