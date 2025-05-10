from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import vectordb
from services.init_vectordb import initialize_vectordb

app = FastAPI(
    title="SK C&C Onboarding RAG API",
    description="VectorDB 관리 및 멀티에이전트 챗봇 API",
    version="1.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(vectordb.router)

# ✅ 서버 시작 시 VectorDB 초기화
@app.on_event("startup")
def on_startup():
    print("🚀 서버 시작됨 - VectorDB 초기화 수행")
    initialize_vectordb()
