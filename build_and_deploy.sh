#!/bin/bash

# Ensure scripts are executable
chmod +x remove_snapshot.sh update_version.sh

# Get current timestamp for log folder and filenames
TIMESTAMP=$(date "+%Y%m%d_%H%M%S")
LOG_DIR="build_logs/logs_${TIMESTAMP}"

# Check if build_logs directory exists; if not, create it
if [ ! -d "build_logs" ]; then
  mkdir "build_logs"
  echo "✅ Created build_logs directory."
else
  echo "✅ build_logs directory already exists."
fi

# Create the timestamped subfolder for logs
mkdir -p "$LOG_DIR"

# Step 1: Git Commit Before Deployment
echo "🔹 Step 1: Staging changes before deployment..."
git add .
git commit -m "Update prior to deploy"

if [ $? -ne 0 ]; then
    echo "⚠️ No changes to commit before deployment."
else
    echo "✅ Changes committed."
fi

echo "🔹 Step 2: Pushing changes to remote (master branch)..."
git push -u origin master

if [ $? -ne 0 ]; then
    echo "❌ Git push failed. Exiting."
    exit 1
fi

echo "✅ Changes pushed successfully."

# Step 3: Remove -SNAPSHOT
echo "🔹 Step 3: Removing -SNAPSHOT from version..."
./remove_snapshot.sh

if [ $? -ne 0 ]; then
    echo "❌ Error removing -SNAPSHOT. Exiting."
    exit 1
fi

echo "✅ -SNAPSHOT removed successfully."

# Step 4: Build project
echo "🔹 Step 4: Running mvn clean package..."
mvn clean package > "$LOG_DIR/package_logs_${TIMESTAMP}.log" 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Maven package build failed. Exiting."
    exit 1
fi

echo "✅ Maven package build successful."

# Step 5: Kill any process running on port 9001 before mvn clean verify
echo "🔹 Step 5: Killing any process running on port 9001 before mvn clean verify..."
lsof -ti:9001 | xargs kill -9

if [ $? -ne 0 ]; then
    echo "⚠️ No process found on port 9001 or failed to terminate."
else
    echo "✅ Process on port 9001 terminated."
fi

# Step 6: Run mvn clean verify
echo "🔹 Step 6: Running mvn clean verify..."
mvn clean verify > "$LOG_DIR/verify_logs_${TIMESTAMP}.log" 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Maven verify failed. Exiting."
    exit 1
fi

echo "✅ Maven verify successful."

# Step 7: Kill any process running on port 9001 after mvn clean verify
echo "🔹 Step 7: Killing any process running on port 9001 after mvn clean verify..."
lsof -ti:9001 | xargs kill -9

if [ $? -ne 0 ]; then
    echo "⚠️ No process found on port 9001 or failed to terminate."
else
    echo "✅ Process on port 9001 terminated."
fi

# Step 8: Deploy project
echo "🔹 Step 8: Running mvn clean deploy..."
mvn clean deploy > "$LOG_DIR/deploy_logs_${TIMESTAMP}.log" 2>&1

if [ $? -ne 0 ]; then
    echo "❌ Maven deploy failed. Exiting."
    exit 1
fi

echo "✅ Maven deploy successful."

# Step 9: Update Version (default: patch)
echo "🔹 Step 9: Updating version and appending -SNAPSHOT..."
./update_version.sh patch  # Change to major/minor if needed

if [ $? -ne 0 ]; then
    echo "❌ Error updating version. Exiting."
    exit 1
fi

echo "✅ Version updated successfully."

# Step 10: Git Commit After Version Update
echo "🔹 Step 10: Staging version update..."
git add .
git commit -m "Version updated"

if [ $? -ne 0 ]; then
    echo "⚠️ No changes to commit after version update."
else
    echo "✅ Version update committed."
fi

echo "🔹 Step 11: Pushing version update to remote (master branch)..."
git push -u origin master

if [ $? -ne 0 ]; then
    echo "❌ Git push failed. Exiting."
    exit 1
fi

echo "✅ Version update pushed successfully."
echo "🎉 Deployment process completed!"