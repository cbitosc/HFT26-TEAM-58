
import os
import traceback

from dotenv import load_dotenv
from openai import OpenAI

# ------------------------------------------------
# 1. Load environment variables
# ------------------------------------------------

load_dotenv()

API_KEY = os.getenv("FEATHERLESS_API_KEY")
BASE_URL = os.getenv(
    "FEATHERLESS_BASE_URL",
    "https://api.featherless.ai/v1"
)
MODEL = os.getenv("FEATHERLESS_MODEL")

# ------------------------------------------------
# 2. Validate configuration
# ------------------------------------------------

if not API_KEY:
    raise ValueError("FEATHERLESS_API_KEY is missing in .env")

if not MODEL:
    raise ValueError("FEATHERLESS_MODEL is missing in .env")

# ------------------------------------------------
# 3. Initialize Featherless client
# ------------------------------------------------

client = OpenAI(
    api_key=API_KEY,
    base_url=BASE_URL,
    timeout=90.0,
    max_retries=1
)

# ------------------------------------------------
# 4. Send prompt to Featherless AI
# ------------------------------------------------

def ask_featherless(prompt: str) -> str:

    if not prompt or not prompt.strip():
        raise ValueError("Prompt cannot be empty")

    try:
        response = client.chat.completions.create(
            model=MODEL,
            messages=[
                {
                    "role": "user",
                    "content": prompt
                }
            ],
            temperature=0.2,
            max_tokens=2048
        )

        # Inspect model response
        choice = response.choices[0]

        print("Model:", MODEL)
        print("Finish reason:", choice.finish_reason)
        print("AI content:", repr(choice.message.content))

        # Extract generated text
        content = choice.message.content

        if isinstance(content, str) and content.strip():
            return content.strip()

        # Some reasoning models may return reasoning
        # without a normal text response.
        print("Empty text response received.")
        print("Response ID:", response.id)
        print("Usage:", response.usage)

        raise RuntimeError(
            "Featherless returned no usable text. "
            "Check model availability and response metadata."
        )

    except Exception as error:
        print("FEATHERLESS API ERROR:", repr(error))
        traceback.print_exc()
        raise
