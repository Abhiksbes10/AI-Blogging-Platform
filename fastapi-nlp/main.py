print("🔥🔥 FINAL CODE LOADED 🔥🔥")

from fastapi import FastAPI
from pydantic import BaseModel
from textblob import TextBlob
from detoxify import Detoxify
import re

app = FastAPI()
model = Detoxify('multilingual')

BAD_WORDS = ["madarchod", "bhosdike", "chutiya", "gaand"]

class TextRequest(BaseModel):
    text: str
    user_id: str


def clean_text(text):
    for word in BAD_WORDS:
        text = re.sub(word, "***", text, flags=re.IGNORECASE)
    return text


@app.post("/analyze")
def analyze(data: TextRequest):

    print("🔥 FUNCTION CALLED")

    original_text = data.text

    result = model.predict(original_text)
    toxicity = float(result["toxicity"])

    blob = TextBlob(original_text)
    sentiment = float(blob.sentiment.polarity)

    print("TOXICITY:", toxicity)
    print("SENTIMENT:", sentiment)

    if toxicity > 0.1 or sentiment < -0.1:
        status = "BLOCKED"
    else:
        status = "ALLOWED"

    print("FINAL STATUS:", status)

    return {
        "status": status,
        "cleaned_text": original_text,
        "score": toxicity,
        "sentiment": sentiment,
        "keywords": [],
        "strikes": 0
    }