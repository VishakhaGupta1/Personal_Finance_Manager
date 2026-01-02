# Render Deployment Guide - Docker Version

## Overview
Since Render doesn't natively support Java, we'll deploy the application as a Docker image. This guide covers everything needed for successful deployment.

## Prerequisites
- GitHub account with the repository pushed ✓
- Render account (https://render.com)
- Docker is optional for local testing

## Step-by-Step Deployment Instructions

### Step 1: Create Render Account
1. Go to [render.com](https://render.com)
2. Click "Sign up" and select "GitHub"
3. Authorize Render to access your GitHub repositories
4. Complete the signup

### Step 2: Deploy Docker Image on Render

1. **Log in to Render Dashboard**
   - Go to https://dashboard.render.com

2. **Create New Web Service**
   - Click "New +" button
   - Select "Web Service"

3. **Connect GitHub Repository**
   - Search for "Personal_Finance_Manager"
   - Select your repository
   - Click "Connect"

4. **Configure Docker Deployment**

   | Setting | Value |
   |---------|-------|
   | **Name** | personal-finance-manager |
   | **Environment** | Docker |
   | **Region** | Choose closest to your location |
   | **Branch** | main |
   | **Dockerfile path** | `Dockerfile` (default) |
   | **Instance Type** | Free |

5. **Docker Build Settings**
   - Render will automatically detect the Dockerfile
   - Build command: Render uses `docker build` by default
   - Start command: Render uses `docker run` by default

6. **Click "Create Web Service"**
   - Render will start building the Docker image
   - This takes 5-10 minutes for the first deployment

### Step 3: Monitor Docker Build

1. **Watch the Build Logs**
   - You'll see Docker build steps:
     - `FROM eclipse-temurin:17-jdk-alpine` (pulling base image)
     - Maven installing dependencies
     - Compiling your application
     - Creating runtime image
   - Look for "Build successful" message

2. **Common Build Messages**
   - "Fetching dependencies..." - Normal, takes 2-3 minutes
   - "Building..." - Maven compilation in progress
   - "Creating image..." - Docker finalizing the image
   - "Pushing..." - Uploading to Render's registry
   - "Running..." - Container starting

3. **Get Your Public URL**
   - Once deployment completes, you'll see: `https://personal-finance-manager-xxxx.onrender.com`
   - Your API will be at: `https://personal-finance-manager-xxxx.onrender.com/api`

### Step 4: Test the Deployment

Once the service shows "Live" status (green):

```bash
# Test if service is running
curl https://personal-finance-manager-xxxx.onrender.com/api/categories

# Run the comprehensive test script
bash financial_manager_tests.sh https://personal-finance-manager-xxxx.onrender.com/api
```

Expected output:
- All 12 tests should pass with correct status codes
- No authentication errors
- Database operations working

### Step 5: Verify Endpoints

```bash
# Register a new user
curl -X POST https://your-render-url/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"test@example.com",
    "password":"Password123!",
    "fullName":"Test User",
    "phoneNumber":"+1234567890"
  }'

# View Swagger UI documentation
# Visit: https://your-render-url/api/swagger-ui/index.html

# Check application health
curl https://your-render-url/api/categories
```

## Docker Files Explanation

### Dockerfile
- **Multi-stage build**: Keeps the final image small
- **Stage 1 (Builder)**: Compiles Maven project using JDK
- **Stage 2 (Runtime)**: Uses lightweight JRE to run the application
- **Health check**: Monitors if the application is responsive

### docker-compose.yml
- Used for local testing with Docker
- Maps port 8080
- Includes health checks
- Auto-restart on failure

## Local Testing with Docker (Optional)

If you have Docker installed locally:

```bash
# Build and run the application
docker-compose up --build

# In another terminal, run tests
bash financial_manager_tests.sh http://localhost:8080/api

# Stop the application
docker-compose down
```

## Important Docker Deployment Notes

### First Deployment
- Docker image build takes 5-10 minutes (longer than native runtimes)
- Image size: ~400-500 MB (normal for Java)
- Subsequent deployments are faster (1-2 minutes)

### Automatic Redeployment
- Enabled by default when you push to main
- Render rebuilds the Docker image
- Application automatically restarts

### Environment Variables
For production configuration, add in Render dashboard:
```
JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

### Health Checks
- Dockerfile includes health checks
- Render uses these to monitor application status
- Automatic restart if health check fails

### Free Tier Limitations
- Automatic shutdown after 15 minutes of inactivity
- First request after sleep takes ~30 seconds
- Image size must be under 500 MB (we're ~400 MB)

## Troubleshooting Docker Deployment

### Build Fails: "Maven not found"
- This is handled automatically in the Dockerfile
- Alpine Linux installs Maven during build
- Check Render build logs for errors

### Build Takes Too Long
- First build with Maven is slow (downloading dependencies)
- Subsequent builds are faster (cached layers)
- Render caches Docker layers between deployments

### Container Starts but API Returns 404
1. Wait 30 seconds for application to fully initialize
2. Check Render logs for startup errors
3. Verify you're using correct URL: `https://your-url/api`
4. Health check may still be running

### "Port 8080 already in use" Error
- Not applicable on Render (runs in isolated container)
- If testing locally, ensure port 8080 is free

### Docker Image Too Large
- Current image: ~400 MB (acceptable)
- Alpine Linux keeps it minimal
- If needed to reduce: use `eclipse-temurin:17-jre-alpine` (already using it)

## Production Optimization (Optional)

For better performance, you can:

1. **Use a container registry** (Docker Hub, GitHub Container Registry)
   - Pre-build and push images
   - Faster deployments on Render

2. **Add persistent database**
   - Replace H2 in-memory database
   - Add PostgreSQL environment variables

3. **Custom domain**
   - Point your domain to the Render service
   - Enable HTTPS (automatic with Let's Encrypt)

## Useful Commands

```bash
# View Render service logs
# (Available in Render dashboard)

# Test service availability
curl -I https://your-render-url/api

# Get detailed response
curl -v https://your-render-url/api/categories

# Stream logs (from Render dashboard)
# Real-time logs available in the "Logs" tab
```

## Deployment Timeline

| Step | Time | Status |
|------|------|--------|
| GitHub → Render notification | Instant | Automated |
| Docker image build | 5-10 min | Watch logs |
| Container startup | 1-2 min | Initialization |
| Health check pass | 30-40 sec | Live status |
| **Total first deployment** | **~10-15 min** | Live! |
| Subsequent deployments | 3-5 min | Faster (cached) |

## Verification Checklist

- [ ] Service shows "Live" status (green)
- [ ] Public URL is accessible
- [ ] `/api/categories` returns 200 OK
- [ ] Test script passes all 12 tests
- [ ] Swagger UI loads at `/api/swagger-ui/index.html`
- [ ] No errors in Render logs

## Support Resources

- **Render Docker Docs**: https://render.com/docs/docker
- **Render General Docs**: https://render.com/docs
- **Spring Boot Docker**: https://spring.io/guides/gs/spring-boot-docker/
- **GitHub Issue**: Create issue in your repository

## Next Steps

1. Follow the step-by-step deployment instructions above
2. Monitor the Docker build in Render dashboard
3. Test using the provided test script
4. Share the URL: `https://your-render-url/api`
5. Monitor application in Render dashboard

---

**Deployment Method**: Docker Image on Render
**Cost**: FREE (free tier)
**Build Time**: 5-10 minutes (first), 1-2 minutes (subsequent)
**Maintenance**: Automatic on git push to main
**Last Updated**: January 2, 2026
