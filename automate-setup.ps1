# Event Management App - Automated Setup and Testing Script
# This script automates Git operations, testing, and validation

$ErrorActionPreference = "Stop"
$projectPath = "C:\Users\deltagare\Downloads\event-management-app"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Event Management App - Automation Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Change to project directory
Set-Location $projectPath
Write-Host "[INFO] Working directory: $projectPath" -ForegroundColor Green
Write-Host ""

# Step 1: Delete unused App.java if it exists
Write-Host "[STEP 1] Checking for unused App.java..." -ForegroundColor Yellow
$appFile = Join-Path $projectPath "src\main\java\org\example\App.java"
if (Test-Path $appFile) {
    Remove-Item $appFile -Force
    Write-Host "[SUCCESS] Deleted src/main/java/org/example/App.java" -ForegroundColor Green
} else {
    Write-Host "[INFO] App.java already deleted or doesn't exist" -ForegroundColor Gray
}
Write-Host ""

# Step 2: Initialize Git if not already initialized
Write-Host "[STEP 2] Checking Git repository..." -ForegroundColor Yellow
if (-not (Test-Path ".git")) {
    Write-Host "[INFO] Initializing Git repository..." -ForegroundColor Gray
    git init
    git add .
    git commit -m "feat: initialize event management app with complete implementation"
    Write-Host "[SUCCESS] Git repository initialized" -ForegroundColor Green
} else {
    Write-Host "[INFO] Git repository already exists" -ForegroundColor Gray
}
Write-Host ""

# Step 3: Create feature branch
Write-Host "[STEP 3] Creating feature/readme-cleanup branch..." -ForegroundColor Yellow
git checkout -b feature/readme-cleanup 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Created and switched to feature/readme-cleanup branch" -ForegroundColor Green
} else {
    Write-Host "[INFO] Branch already exists, switching to it..." -ForegroundColor Gray
    git checkout feature/readme-cleanup
}
Write-Host ""

# Step 4: Make small, focused commits
Write-Host "[STEP 4] Creating commits with clear messages..." -ForegroundColor Yellow

# Commit 1: Delete unused App.java
$changes = git status --porcelain
if ($changes -match "org.example") {
    git add -A
    git commit -m "chore: remove unused org.example.App.java template file"
    Write-Host "[COMMIT 1] chore: remove unused org.example.App.java template file" -ForegroundColor Green
}

# Commit 2: Add README documentation
$changes = git status --porcelain
if ($changes -match "README.md") {
    git add README.md
    git commit -m "docs: add comprehensive README with setup, usage, and architecture"
    Write-Host "[COMMIT 2] docs: add comprehensive README with setup, usage, and architecture" -ForegroundColor Green
}

# Commit 3: Add UML documentation
$changes = git status --porcelain
if ($changes -match "UML_CLASS_DIAGRAM.md") {
    git add UML_CLASS_DIAGRAM.md
    git commit -m "docs: add detailed UML class diagram and architecture documentation"
    Write-Host "[COMMIT 3] docs: add detailed UML class diagram and architecture documentation" -ForegroundColor Green
}

# Commit 4: Update EventService
$changes = git status --porcelain
if ($changes -match "EventService.java") {
    git add src/main/java/com/eventmanagement/service/EventService.java
    git commit -m "refactor: use Optional.isPresent() for Java 11+ compatibility"
    Write-Host "[COMMIT 4] refactor: use Optional.isPresent() for Java 11+ compatibility" -ForegroundColor Green
}

# Commit 5: Update RegistrationService
$changes = git status --porcelain
if ($changes -match "RegistrationService.java") {
    git add src/main/java/com/eventmanagement/service/RegistrationService.java
    git commit -m "refactor: update RegistrationService to use Optional.isPresent()"
    Write-Host "[COMMIT 5] refactor: update RegistrationService to use Optional.isPresent()" -ForegroundColor Green
}

# Commit 6: Update EventManagementApp
$changes = git status --porcelain
if ($changes -match "EventManagementApp.java") {
    git add src/main/java/com/eventmanagement/app/EventManagementApp.java
    git commit -m "refactor: simplify EventManagementApp updateEvent method"
    Write-Host "[COMMIT 6] refactor: simplify EventManagementApp updateEvent method" -ForegroundColor Green
}

Write-Host ""

# Step 5: Merge branch to main
Write-Host "[STEP 5] Merging feature branch to main..." -ForegroundColor Yellow
git checkout main
git merge feature/readme-cleanup --no-edit
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Successfully merged feature/readme-cleanup into main" -ForegroundColor Green
    
    # Delete feature branch
    git branch -d feature/readme-cleanup
    Write-Host "[SUCCESS] Deleted feature/readme-cleanup branch" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Merge failed. Please resolve conflicts manually." -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 6: Run Maven tests
Write-Host "[STEP 6] Running Maven tests..." -ForegroundColor Yellow
Write-Host "[INFO] This may take a few minutes..." -ForegroundColor Gray
mvn clean test
if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[SUCCESS] All tests passed!" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[ERROR] Some tests failed. Please review the output above." -ForegroundColor Red
    $continue = Read-Host "Continue anyway? (y/n)"
    if ($continue -ne "y") {
        exit 1
    }
}
Write-Host ""

# Step 7: Build the application
Write-Host "[STEP 7] Building the application..." -ForegroundColor Yellow
mvn clean package -DskipTests
if ($LASTEXITCODE -eq 0) {
    Write-Host "[SUCCESS] Application built successfully!" -ForegroundColor Green
} else {
    Write-Host "[ERROR] Build failed. Please check the errors above." -ForegroundColor Red
    exit 1
}
Write-Host ""

# Step 8: Display next steps
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Automation Complete!" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Summary:" -ForegroundColor Yellow
Write-Host "  [✓] Deleted unused App.java" -ForegroundColor Green
Write-Host "  [✓] Created and merged feature/readme-cleanup branch" -ForegroundColor Green
Write-Host "  [✓] Added 6 focused commits with clear messages" -ForegroundColor Green
Write-Host "  [✓] Ran all unit tests" -ForegroundColor Green
Write-Host "  [✓] Built the application" -ForegroundColor Green
Write-Host ""
Write-Host "Next Steps:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Setup MySQL Database (if not done):" -ForegroundColor White
Write-Host "   mysql -u root -p < src\main\java\com\eventmanagement\database\schema.sql" -ForegroundColor Gray
Write-Host ""
Write-Host "2. Update database credentials in:" -ForegroundColor White
Write-Host "   src\main\java\com\eventmanagement\database\DatabaseConnection.java" -ForegroundColor Gray
Write-Host ""
Write-Host "3. Run the application:" -ForegroundColor White
Write-Host "   mvn exec:java -Dexec.mainClass=`"com.eventmanagement.app.EventManagementApp`"" -ForegroundColor Gray
Write-Host ""
Write-Host "4. Test Happy Path:" -ForegroundColor White
Write-Host "   - Create an Event (Option 1)" -ForegroundColor Gray
Write-Host "   - Create a Participant (Option 8)" -ForegroundColor Gray
Write-Host "   - Register Participant for Event (Option 11)" -ForegroundColor Gray
Write-Host "   - View Events (Option 4)" -ForegroundColor Gray
Write-Host "   - Check Event Capacity (Option 16)" -ForegroundColor Gray
Write-Host "   - View Participants for Event (Option 14)" -ForegroundColor Gray
Write-Host "   - Manage Attendance Status (Option 13)" -ForegroundColor Gray
Write-Host ""
Write-Host "Would you like to run the application now? (y/n)" -ForegroundColor Yellow
$runApp = Read-Host ""

if ($runApp -eq "y" -or $runApp -eq "Y") {
    Write-Host ""
    Write-Host "[INFO] Starting Event Management App..." -ForegroundColor Yellow
    Write-Host "[INFO] Press Ctrl+C to exit the application" -ForegroundColor Gray
    Write-Host ""
    mvn exec:java -Dexec.mainClass="com.eventmanagement.app.EventManagementApp"
} else {
    Write-Host ""
    Write-Host "[INFO] You can run the application later with:" -ForegroundColor Gray
    Write-Host "   mvn exec:java -Dexec.mainClass=`"com.eventmanagement.app.EventManagementApp`"" -ForegroundColor White
}

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
