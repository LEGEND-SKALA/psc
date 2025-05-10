from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routers import vectordb

app = FastAPI(
    title="SK C&C Onboarding RAG API",
    description="VectorDB 관리 및 멀티에이전트 챗봇 API",
    version="1.0"
)

# CORS 허용 (SpringBoot 연동 시 필요)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 실제 운영시 도메인 지정
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 라우터 등록
app.include_router(vectordb.router)
