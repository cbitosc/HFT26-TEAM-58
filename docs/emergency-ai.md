# Emergency AI Backend

## Overview
AI-powered emergency message processing using the Featherless AI API.

## Features
- Emergency message summarization
- Urgency classification (HIGH, MEDIUM, LOW)
- Message translation into different languages

## Setup
1. Navigate to the ai-backend folder.
2. Install dependencies using pip install -r requirements.txt.
3. Configure the Featherless API key in the .env file.
4. Start the server using uvicorn main:app --reload.
5. Open http://127.0.0.1:8000/docs to access Swagger UI.

## API Endpoints
- GET / - Backend health check
- POST /test-ai - Summarize emergency messages
- POST /classify-emergency - Classify emergency urgency
- POST /translate - Translate emergency messages