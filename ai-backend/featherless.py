import os

from dotenv import load_dotenv
from openai import OpenAI

# Load environment variables from .env
load_dotenv()

# Featherless API configuration
API_KEY = os.getenv("FEATHERLESS_API_KEY")
BASE_URL = os.getenv(
    "FEATHERLESS_BASE_URL",
    "https://api.featherless.ai/v1"
)
MODEL = os.getenv(
    "FEATHERLESS_MODEL",
    "zai-org/GLM-5.2"
)

# Initialize Featherless client
if not API_KEY:
    raise RuntimeError(
        "FEATHERLESS_API_KEY is missing. Add it to your .env file."
    )

client = OpenAI(
    api_key=API_KEY,
    base_url=BASE_URL
)


def ask_featherless(prompt: str) -> str:
    """
    Send a prompt to Featherless AI and return the generated response.
    """

    if not prompt or not prompt.strip():
        raise ValueError("Prompt cannot be empty.")

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
            max_tokens=8192,
            extra_body={
                "chat_template_kwargs": {
                    "enable_thinking": False
                }
            }
        )

        if not response.choices:
            raise RuntimeError("Featherless returned no response.")

        content = response.choices[0].message.content

        if not content or not content.strip():
            raise RuntimeError(
                "Featherless returned an empty response. "
                f"Finish reason: {response.choices[0].finish_reason}"
            )

        return content.strip()

    except Exception as error:
        raise RuntimeError(
            f"Featherless AI request failed: {error}"
        ) from error