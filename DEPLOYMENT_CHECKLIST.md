# Render Deployment Checklist

## Pre-Deployment ✓
- [x] Repository pushed to GitHub
- [x] Test script added (`financial_manager_tests.sh`)
- [x] Build configuration verified in `pom.xml`
- [x] Application starts locally without errors

## Deployment Steps

### 1. Render Account Setup
- [ ] Go to https://render.com
- [ ] Sign up with GitHub account
- [ ] Authorize Render to access your GitHub repositories
- [ ] Verify email

### 2. Create Web Service
- [ ] Log in to https://dashboard.render.com
- [ ] Click "New +" → "Web Service"
- [ ] Connect to "Personal_Finance_Manager" repository
- [ ] Fill in service details:
  - [ ] Name: `personal-finance-manager`
  - [ ] Environment: `Java`
  - [ ] Region: Choose one (e.g., Oregon, Frankfurt)
  - [ ] Branch: `main`
  - [ ] Build Command: `mvn clean install`
  - [ ] Start Command: `java -jar target/finance-manager-1.0.0.jar`
  - [ ] Instance Type: `Free`
- [ ] Click "Create Web Service"

### 3. Wait for Deployment
- [ ] Watch logs in Render dashboard
- [ ] Wait for "Build successful" message
- [ ] Wait for status to show "Live" (green)
- [ ] Note your public URL: `https://personal-finance-manager-xxxx.onrender.com`

### 4. Test Deployment
```bash
# Copy your Render URL and run:
bash financial_manager_tests.sh https://your-render-url/api
```

Expected results:
- [ ] User registration: 201 (Created)
- [ ] User login: 200 (OK)
- [ ] Get categories: 200 (OK)
- [ ] Create category: 201 (Created)
- [ ] Create transaction: 201 (Created)
- [ ] Get transactions: 200 (OK)
- [ ] Create goal: 201 (Created)
- [ ] Get goals: 200 (OK)
- [ ] Monthly report: 200 (OK)
- [ ] Yearly report: 200 (OK)
- [ ] Logout: 200 (OK)

### 5. Manual Testing (Optional)
```bash
# Test user registration
curl -X POST https://your-render-url/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test@example.com","password":"Password123!","fullName":"Test User","phoneNumber":"+1234567890"}'

# View Swagger UI
# Open in browser: https://your-render-url/api/swagger-ui/index.html
```

## Verification

- [ ] API responds at: `https://your-render-url/api`
- [ ] All endpoints return correct status codes
- [ ] Test script shows all tests passing
- [ ] Swagger UI is accessible at `/api/swagger-ui/index.html`
- [ ] Application logs show no errors in Render dashboard

## Useful Links

- **Render Dashboard**: https://dashboard.render.com
- **Your Service Logs**: View in dashboard under "Logs" tab
- **Render Docs**: https://render.com/docs
- **GitHub Repo**: https://github.com/VishakhaGupta1/Personal_Finance_Manager
- **Deployment Guide**: See RENDER_DEPLOYMENT.md in repo

## Troubleshooting Commands

```bash
# Check if service is running
curl -I https://your-render-url/api/categories

# Check service status
curl https://your-render-url/api/categories

# View detailed error (if available)
curl -v https://your-render-url/api/categories
```

## Post-Deployment

- [ ] Share the public URL with users
- [ ] Monitor the service through Render dashboard
- [ ] Enable auto-redeploy on git push (default enabled)
- [ ] Consider upgrading to paid tier if needed for production

---

**Time to Deploy**: ~5-10 minutes
**Cost**: FREE
**Support**: https://render.com/docs
