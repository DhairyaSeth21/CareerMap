# CareerMap Setup Guide

## Quick Start

### 1. Database Setup

Make sure MySQL is running and create the database:

```bash
mysql -u root -p
CREATE DATABASE careermap;
```

### 2. Environment Variables

Set these environment variables before running the backend:

```bash
export DB_PASSWORD=your_mysql_password
export OPENAI_API_KEY=your_openai_api_key
```

### 3. Run Backend

```bash
cd backend
./gradlew clean build
./gradlew bootRun
```

The backend will run on `http://localhost:8080`

### 4. Run Frontend

```bash
cd careermap-ui
npm install
npm run dev
```

The frontend will run on `http://localhost:3000`

## Important Notes

### Quiz Functionality

The quiz system uses OpenAI API to generate questions dynamically. You MUST set the `OPENAI_API_KEY` environment variable for quizzes to work properly.

**Without OpenAI API key:** Quizzes will fall back to preset template questions.

**With OpenAI API key:** Quizzes will generate custom, skill-specific questions using GPT-3.5-turbo.

To get an OpenAI API key:
1. Go to https://platform.openai.com/
2. Sign up or log in
3. Navigate to API Keys section
4. Create a new secret key
5. Export it: `export OPENAI_API_KEY=sk-...`

### Authentication

The app now requires authentication. Access flow:

1. Visit `/landing` - Landing page (public)
2. Click "Get Started" or "Sign In"
3. Login or create account
4. Redirected to dashboard (protected)

All routes except `/landing`, `/login`, `/signup` are protected and require authentication.

To logout: Click on your profile picture in the navbar → Sign Out

## Features

- **AI-Powered Quizzes**: Dynamic question generation based on skill and difficulty
- **Skill Progress Tracking**: Visual analytics and proficiency metrics
- **Career Goal Planning**: Structured paths with readiness scores
- **Job Analysis**: AI comparison of skills vs job requirements
- **Skill Map Visualization**: Interactive learning path with ReactFlow
- **Trend Analytics**: Historical skill development tracking

## Tech Stack

- **Frontend**: Next.js 14, TypeScript, Tailwind CSS
- **Backend**: Spring Boot, Java
- **Database**: MySQL
- **AI**: OpenAI GPT-3.5-turbo
- **Design**: Linear-inspired aesthetic with custom animations
