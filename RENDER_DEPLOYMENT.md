# Render Deployment Guide

## Step-by-Step Deployment Instructions

### Prerequisites
- GitHub account with the repository pushed (✓ Already done)
- Render account (create at https://render.com)

### Step 1: Create Render Account
1. Go to [render.com](https://render.com)
2. Click "Sign up" and select "GitHub"
3. Authorize Render to access your GitHub repositories
4. Complete the signup process

### Step 2: Create New Web Service on Render

1. **Log in to Render Dashboard**
   - Go to https://dashboard.render.com

2. **Create New Service**
   - Click "New +" button in the top right
   - Select "Web Service"

3. **Connect GitHub Repository**
   - Search for "Personal_Finance_Manager" (or your repo name)
   - Select your repository
   - Click "Connect"

4. **Configure Service Settings**

   | Setting | Value |
   |---------|-------|
   | **Name** | personal-finance-manager |
   | **Environment** | Java |
   | **Region** | Choose closest to your location (e.g., Oregon, Frankfurt) |
   | **Branch** | main |
   | **Build Command** | `mvn clean install` |
   | **Start Command** | `java -jar target/finance-manager-1.0.0.jar` |
   | **Instance Type** | Free |

5. **Environment Variables (Optional)**
   - No additional environment variables needed for development
   - For production, you would add database URLs here

6. **Click "Create Web Service"**
   - Render will start the deployment automatically
   - This will take 3-5 minutes for the first deployment

### Step 3: Monitor Deployment

1. **Watch the Deployment Logs**
   - You'll see real-time build logs
   - Wait for "Build successful" message
   - Service will auto-start after build completes

2. **Common Messages**
   - "Building..." - Maven is downloading dependencies and building
   - "Running..." - Application is starting
   - "Live" - Application is ready! (Green status)

3. **Get Your Public URL**
   - Once deployment completes, you'll see a URL like: `https://personal-finance-manager-xxxx.onrender.com`
   - Your API will be available at: `https://personal-finance-manager-xxxx.onrender.com/api`

### Step 4: Test the Deployment

Once the deployment shows "Live" status:

```bash
# Run the test script (adjust the URL with your actual Render URL)
bash financial_manager_tests.sh https://personal-finance-manager-xxxx.onrender.com/api
```

Expected output:
- All tests should pass (status 200/201)
- Test script will validate:
  - User registration and login
  - Category management
  - Transaction creation and retrieval
  - Savings goal operations
  - Report generation
  - User logout

### Step 5: Verify API is Accessible

Test individual endpoints:

```bash
# Register a user
curl -X POST https://personal-finance-manager-xxxx.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test@example.com","password":"Password123!","fullName":"Test User","phoneNumber":"+1234567890"}'

# Get categories
curl https://personal-finance-manager-xxxx.onrender.com/api/categories

# View Swagger UI documentation
# Visit: https://personal-finance-manager-xxxx.onrender.com/api/swagger-ui/index.html
```

## Important Notes

### First Deployment
- Initial deployment takes 3-5 minutes
- Subsequent deployments are faster (1-2 minutes)
- Render automatically redeploys when you push to main branch

### Database
- Uses H2 in-memory database (data is reset on redeploy)
- For persistent data in production, update to PostgreSQL or MySQL

### Logs
- View deployment and runtime logs in Render dashboard
- Helpful for debugging any issues

### Auto-Redeploy
- Enabled by default on main branch
- Disable in service settings if needed

### Free Tier Limits
- Automatic shutdown after 15 minutes of inactivity
- First request after shutdown takes ~30 seconds
- Sufficient for demo/testing purposes

### Environment-Specific Configuration
For production deployment, modify `application.yml`:

```yaml
# For production persistence:
spring:
  datasource:
    url: jdbc:postgresql://your-db-host:5432/financedb
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```

Then add environment variables in Render dashboard:
- `DB_USERNAME`
- `DB_PASSWORD`

## Troubleshooting

### Deployment Fails
1. Check Maven build logs in Render dashboard
2. Ensure pom.xml has correct Java version (17)
3. Verify all dependencies are correct

### Application won't start
1. Check runtime logs in dashboard
2. Ensure port 8080 is available
3. Verify Spring Boot can initialize H2 database

### API returns 404
1. Ensure you're using the correct URL: `https://your-url/api/endpoint`
2. Check that service shows "Live" status
3. Wait a few seconds if service just started

### Tests fail after deployment
1. Wait 30 seconds for service to fully initialize
2. Check if service has enough resources
3. Review application logs in Render dashboard

## Next Steps

After successful deployment:

1. **Share the URL** - Give the `/api` endpoint URL to users
2. **Monitor Performance** - Check Render dashboard for metrics
3. **Add Custom Domain** (optional) - Point your domain to the service
4. **Set up Alerts** (optional) - Get notified of deployment issues
5. **Scale if Needed** - Upgrade to paid tier for better performance

## Support

For Render-specific issues: https://render.com/docs
For Spring Boot issues: https://spring.io/projects/spring-boot
For API documentation: Visit `/api/swagger-ui/index.html` on your deployed URL

---

**Estimated Deployment Time**: 5-10 minutes
**Cost**: FREE (using Render's free tier)
**Maintenance**: Automatic redeploy on git push to main
