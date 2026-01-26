# CareerMap Deployment Guide - Railway.app

**Platform:** Railway.app (Free Tier)
**Estimated Time:** 30 minutes
**Cost:** $0 (Free $5 credit/month)
**Result:** Live URL for your professor to test

---

## Why Railway?

✅ **Free PostgreSQL database** included
✅ **Easy Spring Boot deployment**
✅ **Next.js support** out of the box
✅ **No credit card** required for trial
✅ **GitHub integration** (auto-deploy on push)
✅ **Environment variables** management
✅ **SSL/HTTPS** automatically

---

## Prerequisites

Before starting, you need:
- [ ] GitHub account
- [ ] Code pushed to GitHub repository
- [ ] Railway.app account (free signup)
- [ ] OpenAI API key (for AI explanations)

---

## Step 1: Prepare Your Repository (10 minutes)

### 1.1 Create GitHub Repository

```bash
# Navigate to your project
cd /Users/dhairyaarjunseth/Documents/CareerMap

# Initialize git (if not already)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - CareerMap MVP"

# Create new repo on GitHub (github.com/new)
# Then push:
git remote add origin https://github.com/YOUR_USERNAME/CareerMap.git
git branch -M main
git push -u origin main
```

### 1.2 Update Database Configuration

**Edit:** `/backend/src/main/resources/application.properties`

```properties
# Current (local MySQL):
spring.datasource.url=jdbc:mysql://localhost:3306/careermap
spring.datasource.username=root
spring.datasource.password=${DB_PASSWORD}

# Change to (Railway will inject DATABASE_URL):
spring.datasource.url=${DATABASE_URL:jdbc:mysql://localhost:3306/careermap}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# Change Hibernate dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 1.3 Add PostgreSQL Dependency

**Edit:** `/backend/build.gradle`

```gradle
dependencies {
    // Existing dependencies...
    implementation 'com.mysql:mysql-connector-j:8.1.0'

    // ADD THIS for PostgreSQL:
    implementation 'org.postgresql:postgresql:42.7.1'
}
```

### 1.4 Create Railway Config Files

**Create:** `/backend/railway.json`
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "./gradlew build -x test"
  },
  "deploy": {
    "startCommand": "java -jar build/libs/*.jar",
    "healthcheckPath": "/api/frontier/domains",
    "healthcheckTimeout": 300
  }
}
```

**Create:** `/careermap-ui/railway.json`
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "npm run start",
    "healthcheckPath": "/",
    "healthcheckTimeout": 300
  }
}
```

### 1.5 Update Frontend API URLs

**Edit:** `/careermap-ui/src/app/frontier/page.tsx` (and all other API calls)

Change from:
```typescript
fetch('http://localhost:8080/api/...')
```

To:
```typescript
fetch(`${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080'}/api/...`)
```

**Create:** `/careermap-ui/.env.local`
```bash
NEXT_PUBLIC_API_URL=http://localhost:8080
```

**Create:** `/careermap-ui/.env.production`
```bash
NEXT_PUBLIC_API_URL=https://your-backend.railway.app
```

### 1.6 Commit Changes

```bash
git add .
git commit -m "Configure for Railway deployment"
git push
```

---

## Step 2: Deploy Backend to Railway (10 minutes)

### 2.1 Sign Up & Create Project

1. Go to https://railway.app
2. Click "Start a New Project"
3. Sign in with GitHub
4. Select "Deploy from GitHub repo"
5. Choose your `CareerMap` repository

### 2.2 Add PostgreSQL Database

1. In your Railway project, click "+ New"
2. Select "Database" → "Add PostgreSQL"
3. Railway creates database automatically
4. Note: Database URL will be injected as `DATABASE_URL`

### 2.3 Configure Backend Service

1. Click "+ New" → "GitHub Repo" → Select your repo
2. Railway detects Spring Boot project
3. Set **Root Directory:** `/backend`
4. Click "Deploy"

### 2.4 Set Environment Variables

In Backend Service settings → Variables:

```bash
# Database (Railway auto-injects these from PostgreSQL service)
DATABASE_URL=postgresql://... (auto-injected)

# OpenAI API Key
OPENAI_API_KEY=sk-your-openai-key-here

# Database credentials (if not auto-injected)
DB_USERNAME=postgres
DB_PASSWORD=<from Railway PostgreSQL service>

# Spring Boot profile
SPRING_PROFILES_ACTIVE=production
```

### 2.5 Generate Domain & Wait for Deploy

1. Click "Settings" → "Generate Domain"
2. Railway gives you a URL like: `https://careermap-backend-production.up.railway.app`
3. Wait for build to complete (~5-10 minutes)
4. Check logs for "Started CareerMapBackendApplication"

### 2.6 Verify Backend

```bash
curl https://your-backend.railway.app/api/frontier/domains
# Should return JSON with domains
```

---

## Step 3: Deploy Frontend to Railway (5 minutes)

### 3.1 Add Frontend Service

1. In same Railway project, click "+ New"
2. Select "GitHub Repo" → Your CareerMap repo
3. Set **Root Directory:** `/careermap-ui`
4. Railway detects Next.js project
5. Click "Deploy"

### 3.2 Set Environment Variables

In Frontend Service settings → Variables:

```bash
# Backend URL (use your Railway backend domain)
NEXT_PUBLIC_API_URL=https://your-backend.railway.app

# Node environment
NODE_ENV=production
```

### 3.3 Generate Domain

1. Click "Settings" → "Generate Domain"
2. Railway gives you: `https://careermap-frontend-production.up.railway.app`
3. Wait for build to complete (~3-5 minutes)

### 3.4 Verify Frontend

Open `https://your-frontend.railway.app` in browser
- Should see CareerMap landing page
- Test signup flow
- Verify API calls work

---

## Step 4: Final Configuration (5 minutes)

### 4.1 Fix CORS (If Needed)

If frontend can't connect to backend, add CORS config:

**Create:** `/backend/src/main/java/com/careermappro/config/WebConfig.java`

```java
package com.careermappro.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                    "http://localhost:3000",
                    "https://*.railway.app",
                    "https://your-frontend.railway.app"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

Commit and push:
```bash
git add .
git commit -m "Add CORS configuration"
git push
```

Railway auto-redeploys.

### 4.2 Test Full Flow

1. Navigate to frontend URL
2. Signup with test account
3. Complete calibration
4. Navigate frontier
5. Try AI explanation
6. Start PROBE assessment
7. Verify everything works

---

## Step 5: Share with Professor

### What to Send:

**Email Template:**

```
Subject: CareerMap MVP - Live Demo

Hi Professor [Name],

I've deployed my CareerMap MVP to a live URL for easy testing. No local setup required!

🌐 LIVE URL: https://your-frontend.railway.app

📋 QUICK START:
1. Click "Get Started"
2. Create account (any email/password)
3. Complete calibration (12 questions)
4. Explore the interactive Frontier
5. Try "AI Explanation" on any node
6. Start a PROBE assessment

⏱️ TESTING TIME: ~20 minutes for full flow

📄 DOCUMENTATION:
- Setup Guide: (attach SETUP_GUIDE.md)
- Test Plan: (attach TEST_PLAN.md)

🔑 NOTES:
- AI explanations are powered by OpenAI
- Data persists between sessions
- PostgreSQL database hosted on Railway

Please let me know if you have any questions!

Best regards,
Dhairya Arjun Seth
```

---

## Alternative: Vercel (Frontend Only)

If Railway doesn't work, deploy frontend to Vercel (easier):

```bash
# Install Vercel CLI
npm install -g vercel

# Deploy frontend
cd careermap-ui
vercel

# Follow prompts
# Sets environment variables via dashboard
```

Keep backend on Railway or run locally with ngrok tunnel.

---

## Troubleshooting

### Backend won't start
- Check Railway logs: Click service → Deployments → View Logs
- Common issue: Database connection string format
- Verify `DATABASE_URL` is injected correctly

### Frontend can't connect to backend
- Check CORS configuration
- Verify `NEXT_PUBLIC_API_URL` is set correctly
- Check browser console for errors

### Database tables not created
- Hibernate should auto-create on first run
- Check `spring.jpa.hibernate.ddl-auto=update` in application.properties
- View Railway PostgreSQL logs

### OpenAI API not working
- Verify `OPENAI_API_KEY` is set in backend service
- Check Railway logs for "OpenAI API key not configured" errors

---

## Cost Breakdown

**Railway Free Tier:**
- $5 credit per month
- ~500 hours of usage included
- PostgreSQL database included
- Unlimited projects

**Your Usage:**
- Backend: ~$2-3/month (if left running 24/7)
- Frontend: ~$1-2/month
- Database: $0 (included in base)

**TOTAL:** Free for demo purposes (stays within $5 credit)

**After Free Credit:**
- Can keep running on free tier
- Or upgrade to Hobby plan ($5/month)

---

## Quick Deploy Checklist

Before deploying:
- [ ] Code pushed to GitHub
- [ ] PostgreSQL dependency added to build.gradle
- [ ] Application.properties updated for PostgreSQL
- [ ] Frontend API URLs use environment variable
- [ ] CORS configuration added
- [ ] Railway account created
- [ ] OpenAI API key ready

During deploy:
- [ ] PostgreSQL service created
- [ ] Backend service deployed with env vars
- [ ] Frontend service deployed with env vars
- [ ] Domains generated for both services
- [ ] CORS allows frontend domain

After deploy:
- [ ] Full user flow tested
- [ ] Professor email sent with URL
- [ ] Documentation shared

---

## Expected Timeline

- **Preparation (GitHub + config):** 10-15 minutes
- **Railway backend deploy:** 10-15 minutes
- **Railway frontend deploy:** 5-10 minutes
- **Testing & fixes:** 5-10 minutes
- **TOTAL:** 30-50 minutes

---

## Support

If deployment fails:
1. Check Railway documentation: https://docs.railway.app
2. Railway Discord: https://discord.gg/railway
3. Alternative: Use Render.com (similar process)

---

**You're ready to deploy! 🚀**

Let me know if you hit any issues and I can help debug.
