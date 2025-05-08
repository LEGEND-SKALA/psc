import numpy as np
from PIL import Image
from sentence_transformers import SentenceTransformer
from config import EMBEDDING_MODEL

model = SentenceTransformer(EMBEDDING_MODEL)

def embed_text(text: str) -> np.ndarray:
    return model.encode(text)

def image_to_embedding(image: Image.Image) -> np.ndarray:
    image = image.resize((64, 64)).convert("RGB")
    arr = np.array(image).flatten() / 255.0
    return arr[:384] if arr.size >= 384 else np.pad(arr, (0, 384 - arr.size))
