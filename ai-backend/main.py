
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from featherless import ask_featherless
import traceback

# Initialize FastAPI
app = FastAPI(
    title="Emergency AI Assistant",
    description="AI assistance for emergency communication",
    version="1.0.0"
)


# Request format
class MessageRequest(BaseModel):
    message: str


class TranslationRequest(BaseModel):
    message: str
    target_language: str


# Home endpoint
@app.get("/")
def home():
    return {
        "success": True,
        "message": "Emergency AI backend is running!"
    }


# AI emergency summarization
@app.post("/test-ai")
def test_ai(request: MessageRequest):
    try:
        prompt = (
            "Summarize this emergency message in one short sentence. "
            "Preserve critical details and do not invent information:\n\n"
            + request.message
        )

        result = ask_featherless(prompt)

        return {
            "success": True,
            "response": result
        }

    except Exception as e:
        print("FEATHERLESS ERROR:", repr(e))
        traceback.print_exc()

        raise HTTPException(
            status_code=502,
            detail="AI service is temporarily unavailable."
        )


# Emergency urgency classification
@app.post("/classify-emergency")
def classify_emergency(request: MessageRequest):
    try:
        prompt = (
            "Analyze this emergency message. Classify its urgency as "
            "HIGH, MEDIUM, or LOW. Give a short reason. "
            "This is decision support, not a replacement for human judgment.\n\n"
            + request.message
        )

        result = ask_featherless(prompt)

        return {
            "success": True,
            "classification": result
        }

    except Exception as e:
        print("FEATHERLESS ERROR:", repr(e))
        traceback.print_exc()

        raise HTTPException(
            status_code=502,
            detail="AI classification is temporarily unavailable."
        )


# Emergency message translation
@app.post("/translate")
def translate_message(request: TranslationRequest):
    try:
        prompt = (
            "Translate the following emergency message into "
            + request.target_language
            + ". Preserve all names, locations, numbers and emergency details. "
            "Return only the translation:\n\n"
            + request.message
        )

        result = ask_featherless(prompt)

        return {
            "success": True,
            "translated_message": result
        }

    except Exception as e:
        print("FEATHERLESS ERROR:", repr(e))
        traceback.print_exc()

        raise HTTPException(
            status_code=502,
            detail="Translation service is temporarily unavailable."
        )
