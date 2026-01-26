# Ascent - Railway Deployment Guide

**Quick Deploy: Your Professor Can Use This Live in 15 Minutes**

---

## Prerequisites

1. GitHub account (you already have: DhairyaSeth21)
2. OpenAI API key (get new one from https://platform.openai.com/api-keys)

---

## Step 1: Sign Up for Railway

1. Go to **https://railway.app**
2. Click **"Start a New Project"**
3. Click **"Login with GitHub"**
4. Authorize Railway to access your repositories

---

## Step 2: Create PostgreSQL Database

1. In Railway dashboard, click **"+ New"**
2. Select **"Database"** → **"Add PostgreSQL"**
3. Railway will provision a database automatically
4. Click on the PostgreSQL service
5. Go to **"Variables"** tab
6. **Copy these connection details** (you'll need them):
   - `DATABASE_URL`
   - `PGHOST`
   - `PGPORT`
   - `PGDATABASE`
   - `PGUSER`
   - `PGPASSWORD`

---

## Step 3: Deploy Spring Boot Backend

1. Click **"+ New"** → **"GitHub Repo"**
2. Select **"CareerMap"** repository
3. Railway will detect your repo

### Configure Backend Service:

1. Click on the new service
2. Go to **"Settings"** tab
3. Set **Root Directory**: `/backend`
4. Set **Custom Start Command**: `java -Dserver.port=$PORT -jar build/libs/*.jar`

### Set Environment Variables:

Go to **"Variables"** tab and add:

```bash
# Database connection (use PostgreSQL variables from Step 2)
DB_URL=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}?sslmode=require
DB_USERNAME=${PGUSER}
DB_PASSWORD=${PGPASSWORD}

# OpenAI API Key (get new key from https://platform.openai.com/api-keys)
OPENAI_API_KEY=sk-proj-YOUR_NEW_API_KEY_HERE

# Port (Railway provides this automatically)
PORT=${{PORT}}
```

### Update application.properties for PostgreSQL:

**Important**: You need to update your `backend/src/main/resources/application.properties` to use PostgreSQL instead of MySQL:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# OpenAI API Configuration
openai.api.key=${OPENAI_API_KEY}
```

4. Click **"Deploy"**
5. Wait for build to complete (~3-5 minutes)
6. Once deployed, copy the **public URL** (looks like: `https://yourapp.railway.app`)

---

## Step 4: Deploy Next.js Frontend

1. Click **"+ New"** → **"GitHub Repo"** → Select **"CareerMap"** again
2. Railway will create a new service

### Configure Frontend Service:

1. Click on the new service
2. Go to **"Settings"** tab
3. Set **Root Directory**: `/careermap-ui`
4. Railway will auto-detect Next.js

### Set Environment Variables:

Go to **"Variables"** tab and add:

```bash
# Backend API URL (use the backend URL from Step 3)
NEXT_PUBLIC_API_URL=https://your-backend-url.railway.app
```

### Update Frontend API Calls:

You need to update frontend to use the backend URL. In `careermap-ui/src/lib/api.ts` or wherever you have API calls, change:

```typescript
// Before
const API_URL = 'http://localhost:8080';

// After
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
```

4. Click **"Deploy"**
5. Wait for build to complete (~2-3 minutes)
6. Once deployed, copy the **public URL** (this is what you share with your professor!)

---

## Step 5: Test the Deployment

1. Visit the frontend URL
2. Try to sign up with a test account
3. Complete calibration
4. Check if the frontier map loads

---

## Quick Checklist

- [ ] PostgreSQL database created on Railway
- [ ] Backend deployed with environment variables set
- [ ] Backend is accessible at public URL
- [ ] Frontend deployed with NEXT_PUBLIC_API_URL set
- [ ] Frontend can communicate with backend
- [ ] Test account can sign up and log in
- [ ] Calibration works
- [ ] Frontier map loads

---

## Troubleshooting

### Backend Won't Start
- Check Railway logs: Click service → "Deployments" → Latest deployment → View logs
- Verify all environment variables are set correctly
- Make sure OPENAI_API_KEY is valid

### Frontend Can't Connect to Backend
- Verify NEXT_PUBLIC_API_URL is set correctly
- Check CORS settings in backend (should allow Railway domains)
- Test backend URL directly in browser: `https://your-backend.railway.app/actuator/health`

### Database Connection Failed
- Verify database credentials are correct
- Make sure you're using PostgreSQL dialect (not MySQL)
- Check that DATABASE_URL uses `sslmode=require`

---

## Final URLs

After deployment, you'll have:

- **Frontend (Share this with your professor)**: `https://ascent-frontend-xxx.railway.app`
- **Backend API**: `https://ascent-backend-xxx.railway.app`
- **Database**: Managed by Railway (internal)

---

## Cost

Railway Free Tier includes:
- $5 worth of usage per month
- Sufficient for demo/testing purposes
- Should easily handle your professor's testing

If you need more, Railway has a $5/month hobby plan with more resources.

---

## Next Steps

1. Get a new OpenAI API key
2. Follow steps 1-4 above
3. Share the frontend URL with your professor
4. Celebrate! 🎉

---

**Questions?** Check Railway docs: https://docs.railway.app
