# AI Code Review Agent

An AI-powered code review platform built with **Spring Boot, React, MySQL, and Google Gemini AI**. The application analyzes source code, identifies bugs, evaluates complexity, suggests optimizations, and generates unit tests.

## Features

* AI-powered code analysis using Gemini AI
* Bug detection and code quality assessment
* Time and space complexity analysis
* Optimization suggestions
* Automated unit test generation
* Review history management
* RESTful API architecture
* MySQL database integration

## Tech Stack

### Backend

* Java 17+
* Spring Boot
* Spring Data JPA
* Hibernate
* MySQL
* Gemini AI API

### Frontend

* React
* Axios
* React Router
* CSS Modules

## Project Structure

Backend

```text
src/main/java
├── controller
├── service
├── repository
├── entity
└── dto
```

Frontend

```text
src
├── components
├── services
├── styles
└── assets
```

## Installation

### Backend

```bash
git clone <repository-url>
cd backend
```

Configure:

```properties
gemini.api.key=${GEMINI_API_KEY}
```

Run:

```bash
mvn spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Future Improvements

* Authentication & Authorization
* Multi-model AI support
* Code similarity detection
* Export reports as PDF
* Team collaboration features
