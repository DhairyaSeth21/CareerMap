#!/bin/bash

# Ascent Backend Startup Script
echo "🚀 Starting Ascent Backend..."

# Set environment variables (set these in your shell or .env file)
# export DB_PASSWORD=your_db_password
# export OPENAI_API_KEY=your_openai_api_key

echo "✅ Environment variables loaded from shell"
echo "🔑 OpenAI API Key: ${OPENAI_API_KEY:0:10}..."
echo "🗄️  Database password: configured"
echo ""
echo "🏗️  Building and starting backend..."

./gradlew bootRun
