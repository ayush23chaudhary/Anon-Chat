# AnonChat Deployment to Render.com (Docker + Free Tier)

## 📋 Overview

This guide deploys your AnonChat backend to **Render.com** using Docker. Render is:
- ✅ **Free tier** - No credit card required
- ✅ **GitHub integration** - Auto-deploys on push
- ✅ **Docker support** - Uses your Dockerfile
- ✅ **WebSocket compatible** - Perfect for chat apps
- ⚠️ **Free tier limit** - Spins down after 15 minutes of inactivity

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                   Your AnonChat Stack                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Frontend (React/Vite)    →    Backend (Spring Boot)    │
│  ├─ Vercel.com               ├─ Render.com (Docker)    │
│  │  https://anonchat         │  https://anonchat-api   │
│  │  .vercel.app              │  .onrender.com          │
│  │                           │                         │
│  └─── WebSocket Connection ──┘                         │
│                                  ↓                      │
│                           Database                      │
│                           ├─ Supabase                  │
│                           │  PostgreSQL                │
│                           └─ db.rtxijsgj...           │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## Prerequisites

✅ GitHub account (with `Anon-Chat` repo)
✅ Supabase PostgreSQL database (from Phase 2)
✅ Dockerfile in your repo (already created)

---

## Phase 3B: Backend Deployment to Render (Docker)

### Step 3B.1: Create Render Account

1. Navigate to [https://render.com](https://render.com)
2. Click **"Get Started"** (top right)
3. Sign up with **GitHub** (recommended)
4. Authorize Render to access your repositories
5. You'll be redirected to the Render dashboard

### Step 3B.2: Create a New Web Service

1. In the Render dashboard, click **"New +"** (top right)
2. Select **"Web Service"**
3. A list of your GitHub repos will appear
4. Find and click **`Anon-Chat`** repository
5. Click **"Connect"** (or "Import" if needed)

### Step 3B.3: Configure the Web Service

**Service Settings:**

| Setting | Value | Notes |
|---------|-------|-------|
| **Name** | `anonchat-api` | Your app's URL slug |
| **Region** | `Singapore` or closest to you | For lower latency |
| **Branch** | `main` | Deploy from main branch |
| **Runtime** | `Docker` | Use Dockerfile |
| **Dockerfile Path** | `Dockerfile` | Root directory |

**Key Configuration Steps:**

1. **Set the Service Name**: Change to `anonchat-api`
   - Your URL will be: `https://anonchat-api.onrender.com`

2. **Select Region**: Choose the region closest to your users
   - Options: Singapore, Tokyo, Frankfurt, Ohio, etc.
   - Lower latency = better user experience

3. **Confirm Branch**: Should be `main`

4. **Confirm Runtime**: Should be `Docker`
   - Render will auto-detect your `Dockerfile`

5. **Plan**: Select **"Free"** tier
   - $0/month (spins down after 15 min inactivity)
   - Adequate for dev/testing

### Step 3B.4: Add Environment Variables

Scroll down to **"Environment"** section:

Click **"Add Environment Variable"** and add these three:

**Variable 1: DB_URL**
```
Name: DB_URL
Value: jdbc:postgresql://db.rtxijsgjrmqatmnrgkzt.supabase.co:5432/postgres?user=postgres&password=2%25W%3FRc3%40%26cxPBAA
```
⚠️ **IMPORTANT**: Special characters in the password MUST be URL-encoded:
- `%` → `%25`
- `?` → `%3F`
- `@` → `%40`
- `&` (in password itself) → `%26`

**Variable 2: DB_DIALECT**
```
Name: DB_DIALECT
Value: org.hibernate.dialect.PostgreSQLDialect
```

**Variable 3: DB_DRIVER**
```
Name: DB_DRIVER
Value: org.postgresql.Driver
```

**Variable 4: DB_DDL_AUTO (Optional)**
```
Name: DB_DDL_AUTO
Value: update
```
This safely creates/updates schema without dropping tables. Use `create-drop` only for testing.

⚠️ **IMPORTANT**: These are **secret environment variables**. Render encrypts them and doesn't expose them in logs or source code.

### Step 3B.5: Deploy

1. Scroll to the bottom
2. Click **"Create Web Service"**
3. Render will:
   - Clone your GitHub repo
   - Build the Docker image
   - Push it to Render's registry
   - Start your container
4. Watch the **Logs** section for build progress

**Build typically takes 3-5 minutes.**

You'll see messages like:
```
[INFO] Building AnonChat API Gateway...
[INFO] Downloading dependencies...
[INFO] Building JAR...
[INFO] Docker image built successfully
```

### Step 3B.6: Wait for "Live" Status

Once the build completes, you'll see:
- ✅ **Status**: `Live` (green indicator)
- 📍 **URL**: `https://anonchat-api.onrender.com`
- 📊 **Logs**: Showing Spring Boot startup messages

**Copy your Render URL** - you'll need it for Vercel setup!

### Step 3B.7: Verify Backend is Running

Test your deployed backend:

```bash
# Test the API is accessible
curl https://anonchat-api.onrender.com/api/actuator/health

# You should get a response like:
# {"status":"UP"}
```

If you get a response, your backend is live! ✅

---

## Understanding the Dockerfile

Here's what happens in your `Dockerfile`:

**Stage 1: Builder**
```dockerfile
FROM maven:3.9-eclipse-temurin-17 as builder
```
- Uses Maven 3.9 + Java 17
- Builds your Spring Boot JAR

**Stage 2: Runtime**
```dockerfile
FROM eclipse-temurin:17-jre-alpine
```
- Lightweight Alpine Linux with Java 17 runtime only
- **Final image size**: ~300MB (vs 1GB+ with full JDK)
- Much faster to deploy and start

**Health Check**
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --retries=3
```
- Render periodically checks if your app is healthy
- Auto-restarts if health check fails
- Essential for reliability

---

## Deployment Workflow

Every time you push to GitHub:

```
Local Machine
     ↓
git push origin main
     ↓
GitHub receives push
     ↓
GitHub → Render (webhook notification)
     ↓
Render pulls latest code
     ↓
Builds Docker image
     ↓
Deploys to container
     ↓
Your backend automatically updated!
```

**No manual deployment needed** - it's fully automated! 🚀

---

## Monitoring Your Render Deployment

### View Logs

1. In Render dashboard, click your service
2. Go to **"Logs"** tab
3. See real-time logs from your Spring Boot app

### Common Log Entries

✅ **Good signs:**
```
2026-03-31 15:46:33 [main] INFO  c.a.gateway.ApiGatewayApplication - Started ApiGatewayApplication in X seconds
2026-03-31 15:46:33 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer - Tomcat started on port 8081
2026-03-31 15:46:33 [main] INFO  com.zaxxer.hikari.HikariDataSource - HikariPool-1 - Added connection
```

❌ **Bad signs:**
```
ERROR: Database connection refused
ERROR: Unable to build Hibernate SessionFactory
ERROR: Failed to execute goal
```

### Redeploy Manually

If something goes wrong:
1. Click **"Manual Deploy"** button
2. Select branch (`main`)
3. Click **"Deploy"**

---

## Render Free Tier Limitations & Solutions

### Limitation 1: Spins Down After 15 Minutes Inactivity

**What happens:**
- After 15 minutes with no requests, Render puts your app to sleep
- Next request takes 30-60 seconds to wake up
- Users see a delay on first chat message

**Solution for Development:**
- This is acceptable during dev/testing
- Upgrade to **paid tier** ($7/month) for always-on

### Limitation 2: Limited Build Time

**What happens:**
- Free tier has 1-hour build timeout
- Your Maven build typically takes 3-5 minutes (no problem)

**Solution:**
- Always use `-DskipTests` in build command to save time

### Limitation 3: Limited Outbound Bandwidth

**What happens:**
- Free tier has limited bandwidth
- Your chat app will hit limits only with thousands of concurrent users

**Solution for Production:**
- Use paid tier ($7/month) for unlimited bandwidth

---

## Next Steps: Connect Frontend to Render Backend

Once your Render backend is deployed and showing ✅ **Live**:

1. **Copy your Render URL**: `https://anonchat-api.onrender.com`

2. **Update Vercel with your Render URL** (Phase 4):
   - `VITE_API_URL` = `https://anonchat-api.onrender.com/api`
   - `VITE_WS_URL` = `wss://anonchat-api.onrender.com/api/ws/chat`

3. **Redeploy Vercel** to pick up the new environment variables

4. **Test end-to-end**:
   - Open your Vercel frontend URL
   - Create a chat room
   - Send a message
   - Verify it persists in Supabase

---

## Troubleshooting

### Issue: Build Fails with "Maven not found"

**Solution:**
- Make sure `pom.xml` is in the root directory
- Render auto-detects it during build

### Issue: Port Already in Use

**Solution:**
- Your `application.properties` sets `server.port=8081`
- This is correct for Docker
- Render internally routes port 8081

### Issue: Database Connection Refused

**Solution:**
- Double-check `DB_URL` environment variable
- Verify Supabase database is running
- Ensure network access is allowed

### Issue: Spins Down Too Often

**Solution:**
- Keep your app warm with periodic requests
- Or upgrade to paid tier ($7/month)

---

## Summary Checklist ✨

- [ ] Dockerfile created in project root
- [ ] Render account created
- [ ] GitHub repo connected to Render
- [ ] Web Service created with name `anonchat-api`
- [ ] Environment variables added (DB_URL, DB_DIALECT, DB_DRIVER)
- [ ] Build completed successfully (status: Live)
- [ ] Backend URL copied: `https://anonchat-api.onrender.com`
- [ ] Health check passed: `curl https://anonchat-api.onrender.com/api/actuator/health`
- [ ] Ready for Phase 4: Frontend deployment to Vercel!

---

## Quick Reference: Your Deployment URLs

Once all phases are complete:

```
Frontend:  https://anonchat.vercel.app
Backend:   https://anonchat-api.onrender.com
Database:  PostgreSQL on Supabase
```

Your users can chat at: **https://anonchat.vercel.app** 🌍🔒

---

**Congratulations! Your Spring Boot backend is now deployed to Render!** 🎉

## Step 8: Update Vercel Environment Variables

Go to your Vercel project settings and update:
```
VITE_API_URL = https://anonchat-backend.onrender.com/api
VITE_WS_URL = wss://anonchat-backend.onrender.com/api/ws/chat
```

## ⚠️ Important Notes

**Free Tier Limitations:**
- ✅ Web services spin down after 15 minutes of inactivity
- ✅ Takes ~30 seconds to wake up on first request
- ✅ Perfect for development and testing
- ✅ Suitable for small production apps

**To Keep Backend Always On:**
- Upgrade to Render's paid plan ($7/month)
- Or keep the free tier for testing

**Your Backend Will:**
- Auto-deploy when you push to GitHub
- Auto-scale on demand
- Support WebSocket connections
- Connect to Supabase PostgreSQL

## Next Steps
1. Deploy backend on Render (this guide)
2. Update Vercel with new Render URLs
3. Test the full stack!

---

**Congratulations!** You now have a fully free, credit-card-free deployment stack:
- ✅ Frontend: Vercel (free)
- ✅ Backend: Render (free)
- ✅ Database: Supabase (free)
- ✅ Total Cost: **$0/month** 🎉
