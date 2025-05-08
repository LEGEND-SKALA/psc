import os
from dotenv import load_dotenv

# 데이터 경로 설정
DATA_DIRS = {
    "regulation": {
        "raw": "./data/raw/regulation",
        "processed": "./data/processed/regulation",
        "embeddings": "./data/embeddings/regulation"
    },
    "space": {
        "raw": "./data/raw/space",
        "processed": "./data/processed/space",
        "embeddings": "./data/embeddings/space"
    }
}

VECTORDB_DIRS = {
    "regulation": "./vectordb/regulation_db",
    "space": "./vectordb/space_db"
}

EMBEDDING_MODEL = "sentence-transformers/all-MiniLM-L6-v2"

# 환경 변수 로드
load_dotenv()
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
