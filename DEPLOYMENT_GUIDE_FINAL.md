# AnonChat Deployment Guide (Vercel + Koyeb + Supabase)

## 📋 Overview
This guide provides **step-by-step instructions** to deploy AnonChat across three platforms:
- **Frontend**: Vercel (React/Vite)
- **Backend**: Koyeb (Spring Boot API Gateway)
- **Database**: Supabase (PostgreSQL)

---

## Phase 1: Codebase Preparation ⚙️

Before deploying to any platform, you must configure your codebase for production.

### Step 1.1: Add PostgreSQL Driver to Backend

Edit `api-gateway/pom.xml` and locate the `<dependencies>` section. Add the PostgreSQL driver dependency:

```xml
<!-- Add this inside <dependencies> block -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

**Why?** Your backend currently uses H2 (in-memory). Supabase uses PostgreSQL, so you need the driver to connect.

### Step 1.2: Configure Dynamic Environment Variables in Backend

Edit `api-gateway/src/main/resources/application.properties`:

```properties
# Database Configuration - Uses environment variables from Koyeb
# Falls back to H2 if variables are not set (local development)
spring.datasource.url=${DB_URL:jdbc:h2:mem:anonchat}
spring.datasource.username=${DB_USER:SA}
spring.datasource.password=${DB_PASSWORD:}
spring.datasource.driver-class-name=${DB_DRIVER:org.h2.Driver}

# Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=${DB_DIALECT:org.hibernate.dialect.H2Dialect}

# Server Port
server.port=8081
server.servlet.context-path=/api
```

**Why?** This allows your backend to work locally with H2 AND in production with Supabase's PostgreSQL by reading environment variables.

### Step 1.3: Update Frontend to Use Environment Variables

Edit `frontend/src/services/ChatService.js` (or wherever you initialize API/WebSocket connections):

**Look for hardcoded URLs like:**
```javascript
const socket = new WebSocket('ws://localhost:8081/ws/chat');
const response = await fetch('http://localhost:8081/api/...');
```

**Replace with environment variables:**
```javascript
// At the top of your file
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api';
const WS_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8081/ws/chat';

// Then use these variables
const socket = new WebSocket(WS_URL);
const response = await fetch(`${API_BASE_URL}/your-endpoint`);
```

**Why?** Vercel can inject different URLs at build time, so your production frontend automatically talks to your production Koyeb backend.

### Step 1.4: Commit and Push Changes

```bash
cd /Users/ayushchaudhary/Projects/AnonChat

git add api-gateway/pom.xml
git add api-gateway/src/main/resources/application.properties
git add frontend/src/services/ChatService.js
git commit -m "chore: prepare for production deployment with postgres and env vars"
git push origin main
```

---

## Phase 2: Database Setup (Supabase) 🗄️

### Step 2.1: Create Supabase Account

1. Navigate to [https://supabase.com/](https://supabase.com/)
2. Click **"Start your project"** in the top right
3. Sign in with GitHub or create a new account
4. Select **"Create a new project"** under an organization (or create one)

### Step 2.2: Create a New Project

1. Fill in the form:
   - **Project Name**: `AnonChatDB` (or your preferred name)
   - **Database Password**: Generate a strong password (copy this somewhere safe!) "2%W?Rc3@&cxPBAA"
   - **Region**: Select the region closest to where your Koyeb backend will be (e.g., `us-east-1` if deploying in US)
   - **Pricing Plan**: Keep **Free** selected

2. Click **"Create new project"**
3. Wait 2-3 minutes for the database to initialize. You'll see a loading screen.

### Step 2.3: Retrieve Database Connection String

Once the project is ready:

1. In the left sidebar, click **"Project Settings"** (gear icon at the bottom)
2. Go to **"Database"** tab
3. Scroll down to **"Connection String"**
4. Click the **"JDBC"** tab (important!)
5. You'll see a connection string like:
   ```
   jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres?user=postgres.xxxx&password=[YOUR-PASSWORD]
   ```

6. **Copy this entire string** and save it somewhere (you'll need it in Phase 3)
7. Replace `[YOUR-PASSWORD]` in the string with your actual database password from Step 2.2

**Your AnonChat Connection String:**
```
jdbc:postgresql://db.rtxijsgjrmqatmnrgkzt.supabase.co:5432/postgres?user=postgres&password=2%W?Rc3@&cxPBAA
```

⚠️ **IMPORTANT**: Keep this connection string safe! Do NOT commit it to GitHub. You will paste it as an environment variable in Koyeb (Step 3.4).

### Step 2.4: Verify Connection (Optional)

You can test the connection locally using environment variables:

**Option 1: Set environment variables before running (Recommended)**
```bash
cd /Users/ayushchaudhary/Projects/AnonChat/api-gateway
export DB_URL="jdbc:postgresql://db.rtxijsgjrmqatmnrgkzt.supabase.co:5432/postgres?user=postgres&password=2%W?Rc3@&cxPBAA"
export DB_DIALECT="org.hibernate.dialect.PostgreSQLDialect"
export DB_DRIVER="org.postgresql.Driver"
mvn spring-boot:run
```

**Option 2: Using Java system properties**
```bash
cd /Users/ayushchaudhary/Projects/AnonChat/api-gateway
mvn spring-boot:run \
  -Dspring-boot.run.arguments="--spring.datasource.url=jdbc:postgresql://db.rtxijsgjrmqatmnrgkzt.supabase.co:5432/postgres?user=postgres&password=2%W?Rc3@&cxPBAA" \
  -Dspring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

If it connects successfully, you'll see:
- ✅ "HikariPool-1 - Added connection conn0: url=jdbc:postgresql://..." in the logs
- ✅ PostgreSQL table creation statements (not H2)
- ✅ "Started ApiGatewayApplication in X seconds"

✅ **Connection verified!** Your Supabase database is ready for production.

**Troubleshooting**: If you see "jdbc:h2:mem:anonchat" in the logs, the environment variables weren't picked up. Use Option 1 (export) for better results.

---

## Phase 3: Backend Deployment (Koyeb) 🚀

### Step 3.1: Create Koyeb Account

1. Navigate to [https://www.koyeb.com/](https://www.koyeb.com/)
2. Click **"Start for free"**
3. Sign up with **GitHub** (recommended for easy CI/CD)
4. Authorize Koyeb to access your GitHub repositories

### Step 3.2: Create New App on Koyeb

1. After login, click **"Create App"** (top right)
2. Select **"GitHub"** as the deployment method
3. You'll be asked to authorize Koyeb with GitHub if not already done
4. Select your **`Anon-Chat`** repository
5. Select **`main`** branch

### Step 3.3: Configure Build & Deployment Settings

In the "Service Settings" section:

1. **Builder**: Leave as **"Buildpack"** (Koyeb auto-detects Maven/Spring Boot)
   
2. **Work Directory**: Click and set to **`api-gateway`** (the subfolder containing pom.xml)

3. **Run Command**: Override the default with:
   ```
   java -jar target/api-gateway-1.0.0.jar
   ```
   *(Check your pom.xml `<finalName>` tag to confirm the exact jar name)*

4. **Port Mapping**: Ensure the port is **8081** (Spring Boot default)

### Step 3.4: Add Environment Variables

Click **"Environment Variables"** and add the following:

| Key | Value | Notes |
|-----|-------|-------|
| `DB_URL` | `jdbc:postgresql://db.rtxijsgjrmqatmnrgkzt.supabase.co:5432/postgres?user=postgres&password=2%W?Rc3@&cxPBAA` | Your Supabase JDBC connection string |
| `DB_DIALECT` | `org.hibernate.dialect.PostgreSQLDialect` | Tells Hibernate to use PostgreSQL syntax |
| `DB_DRIVER` | `org.postgresql.Driver` | PostgreSQL JDBC driver class |

**Step-by-step in Koyeb UI:**
1. Scroll to "Environment Variables" section
2. Click "Add Environment Variable"
3. Enter Key: `DB_URL`
4. Enter Value: `jdbc:postgresql://db.rtxijsgjrmqatmnrgkzt.supabase.co:5432/postgres?user=postgres&password=2%W?Rc3@&cxPBAA`
5. Click "Add" to add the next variable
6. Enter Key: `DB_DIALECT`
7. Enter Value: `org.hibernate.dialect.PostgreSQLDialect`
8. Click "Add" one more time
9. Enter Key: `DB_DRIVER`
10. Enter Value: `org.postgresql.Driver`

⚠️ **NOTE**: These credentials are environment variables in Koyeb. They are NOT hardcoded in your Git repo, so they're safe.

### Step 3.5: Select Instance Type

1. Under **"Instance Type"**, select **"Free"** (Eco)
2. This gives you enough resources for a chat app, and **importantly, Koyeb doesn't force servers to sleep** after inactivity (unlike Heroku), which is critical for WebSocket chat rooms

### Step 3.6: Deploy

1. Click **"Deploy"** (bottom of the form)
2. Koyeb will:
   - Clone your GitHub repo
   - Build the Maven project (`mvn clean package`)
   - Start your Spring Boot app
3. Wait for the status to turn **green** (usually takes 3-5 minutes)

### Step 3.7: Get Your Koyeb Backend URL

1. Once deployment is successful, look at the top of the Koyeb dashboard
2. You'll see a **Public URL** like: `https://my-app-name-xyz.koyeb.app`
3. **Copy this URL** (you'll need it for Vercel in Phase 4)

**Example:**
```
Backend API Base: https://my-app-name-xyz.koyeb.app/api
WebSocket URL: wss://my-app-name-xyz.koyeb.app/ws/chat
```

### Step 3.8: Verify Backend is Running

Test your backend is accessible:
```bash
curl https://my-app-name-xyz.koyeb.app/api/health
```

If it returns a response (or a valid error), the backend is live!

---

## Phase 4: Frontend Deployment (Vercel) 🎨

### Step 4.1: Go to Vercel

1. Navigate to [https://vercel.com/](https://vercel.com/)
2. Click **"Log in"** and select **GitHub**
3. Authorize Vercel to access your GitHub account

### Step 4.2: Import Your Repository

1. After login, click **"Add New"** (top left)
2. Select **"Project"**
3. Under "Import Git Repository", find and click **`Anon-Chat`**
4. Click **"Import"**

### Step 4.3: Configure Project Settings

1. **Framework Preset**: Vercel will auto-detect **Vite** (leave as is)

2. **Root Directory**: 
   - Click **"Edit"** next to the directory selector
   - Select **`frontend`** folder
   - Click **"Select"**

3. **Build Command**: Leave as default (Vercel detects `npm run build` from package.json)

4. **Output Directory**: Leave as default (`dist`)

### Step 4.4: Add Environment Variables

1. Scroll down to **"Environment Variables"**
2. Click **"Add Environment Variable"** twice to add:

**First Variable:**
- **Name**: `VITE_API_URL`
- **Value**: `https://my-app-name-xyz.koyeb.app/api`
  *(Replace with your actual Koyeb URL from Phase 3.7)*

**Second Variable:**
- **Name**: `VITE_WS_URL`
- **Value**: `wss://my-app-name-xyz.koyeb.app/ws/chat`
  *(Replace with your actual Koyeb URL, but use `wss://` for secure WebSockets)*

**Example (filled in):**
```
VITE_API_URL = https://anonchat-backend-abc123.koyeb.app/api
VITE_WS_URL = wss://anonchat-backend-abc123.koyeb.app/ws/chat
```

### Step 4.5: Deploy

1. Click **"Deploy"** (bottom of page)
2. Vercel will:
   - Clone your repo
   - Install dependencies
   - Build the Vite app
   - Deploy to their CDN
3. Wait for the status to show **"Ready"** (usually 2-3 minutes)

### Step 4.6: Get Your Vercel Frontend URL

Once deployment is complete, you'll see a **Domains** section with your live URL:
```
https://anonchat.vercel.app
```

**Copy this URL and test it!**

---

## Phase 5: Testing Your Full Stack ✅

### Step 5.1: Test Frontend

1. Open your Vercel URL in a browser: `https://anonchat.vercel.app`
2. You should see the Landing page with your Charcoal/Indigo theme
3. Click **"Start Secure Chat"** and verify the authentication flow works

### Step 5.2: Test WebSocket Connection

1. Open your browser's **Developer Console** (F12 or Cmd+Option+I)
2. Go to the **Network** tab, then **WS** filter
3. Join or create a chat room
4. You should see WebSocket connections like:
   ```
   wss://anonchat-backend-abc123.koyeb.app/ws/chat/room-XXXXX
   ```
5. If you see messages being sent/received, your WebSocket is working! ✅

### Step 5.3: Test Database Connection

1. From two different browser windows/tabs, join the same room
2. Send a message from one
3. Verify it appears in the other ✅
4. Refresh the page — if the message persists, your Supabase database is working!

### Step 5.4: Common Issues & Fixes

**Issue: "WebSocket connection failed" or "Failed to connect to backend"**
- **Cause**: CORS or WebSocket origin mismatch
- **Fix**: Update your Spring Security config in `api-gateway/src/main/java/.../WebSocketConfig.java`:
  ```java
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration configuration = new CorsConfiguration();
      configuration.setAllowedOrigins(Arrays.asList(
          "https://anonchat.vercel.app"  // Add your Vercel URL here
      ));
      configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
      configuration.setAllowCredentials(true);
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
  }
  ```
- Then commit, push, and Koyeb will auto-redeploy

**Issue: "Database connection refused"**
- **Cause**: Wrong JDBC URL or credentials in Koyeb environment variables
- **Fix**: Double-check your `DB_URL` in Koyeb dashboard matches your Supabase connection string

**Issue: Koyeb build fails**
- **Cause**: Maven can't find dependencies or Java version mismatch
- **Fix**: Check Koyeb deployment logs. Common fix is updating `pom.xml` with correct Java version

---

## Phase 6: Ongoing Maintenance 🔧

### Auto-Deployment on Git Push

All three platforms (Vercel, Koyeb, Supabase) are connected to your GitHub repo. When you push changes:

1. **Frontend changes** → Vercel auto-rebuilds in ~1 minute
2. **Backend changes** → Koyeb auto-rebuilds in ~3-5 minutes
3. **Database schema** → Hibernate auto-migrates when backend starts

### Example Workflow:
```bash
# Make code changes locally
git add .
git commit -m "feat: add new chat feature"
git push origin main

# Vercel frontend redeploys automatically
# Koyeb backend redeploys automatically
# Supabase schema updates when backend starts
```

### Monitor Your Apps

- **Vercel**: Dashboard shows build status, logs, and analytics
- **Koyeb**: Dashboard shows app status, memory/CPU usage, and deployment logs
- **Supabase**: Dashboard shows database connections, query performance, and storage usage

---

## Phase 7: Scaling & Production Hardening 📈

Once everything is working, consider these improvements:

### 1. Custom Domain
- In Vercel, add your own domain (e.g., `anonchat.com`)
- DNS records will be auto-managed by Vercel

### 2. Analytics & Monitoring
- Enable Vercel Web Analytics
- Monitor Koyeb app performance
- Set up Supabase alerts for database issues

### 3. Backup Strategy
- Supabase offers automated daily backups on paid plans
- Free tier: manually export data if needed

### 4. Rate Limiting
- Add Spring Boot rate limiting to prevent abuse
- Vercel handles DDoS protection automatically

### 5. SSL/TLS
- All three platforms use automatic SSL (https/wss)
- Your chat is encrypted in transit by default ✅

---

## Summary Checklist ✨

- [ ] Phase 1: Updated pom.xml, application.properties, and frontend env vars
- [ ] Phase 1: Committed and pushed changes to GitHub
- [ ] Phase 2: Created Supabase account and project
- [ ] Phase 2: Retrieved and saved JDBC connection string
- [ ] Phase 3: Created Koyeb account and app
- [ ] Phase 3: Added environment variables to Koyeb
- [ ] Phase 3: Deployment successful (green status)
- [ ] Phase 3: Copied Koyeb public URL
- [ ] Phase 4: Connected Vercel to GitHub repo
- [ ] Phase 4: Added VITE_API_URL and VITE_WS_URL environment variables
- [ ] Phase 4: Deployment successful
- [ ] Phase 4: Copied Vercel frontend URL
- [ ] Phase 5: Tested frontend in browser
- [ ] Phase 5: Tested WebSocket connections
- [ ] Phase 5: Tested database persistence
- [ ] Production site is live! 🎉

---

## Support & Troubleshooting 🆘

If you encounter issues:

1. **Check Platform Logs**:
   - Vercel: Deployments tab
   - Koyeb: Logs section
   - Supabase: Logs or SQL editor

2. **Test Locally First**:
   - Run backend locally: `mvn spring-boot:run`
   - Run frontend locally: `npm run dev`
   - Verify on localhost before deploying

3. **Common Fixes**:
   - Clear browser cache (Ctrl+Shift+Del or Cmd+Shift+Del)
   - Redeploy the backend (Koyeb -> Redeploy)
   - Check environment variables are correctly set

---

**Congratulations! Your AnonChat is now live and globally accessible!** 🌍🔒

