from fastapi import FastAPI
from pydantic import BaseModel
from fastapi.middleware.cors import CORSMiddleware
from agents.router_agent import route
from utils.vectordb.document import add_document, list_documents, delete_document
from typing import List

app = FastAPI(
    title="AI Engine API",
    description="VectorDB + Agent + 문서 관리 API",
    version="1.0.0",
)

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 배포시에는 특정 도메인만 허용하는 것이 좋습니다.
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# -------------------------
# 모델 정의
# -------------------------

class Query(BaseModel):
    user_input: str

class DocumentAddRequest(BaseModel):
    target: str  # regulation 또는 space
    file_path: str
    security_level: str = "중"

class DocumentDeleteRequest(BaseModel):
    target: str
    doc_source_name: str

# -------------------------
# 기본 API
# -------------------------

@app.get("/")
def read_root():
    return {"message": "AI Engine is running"}

# -------------------------
# Chat API
# -------------------------

@app.post("/chat")
def chat(query: Query):
    """
    사용자 질문 → 적절한 Agent → 답변 반환
    """
    response = route(query.user_input)
    return {"response": response}

# -------------------------
# 문서 관리 API
# -------------------------

@app.post("/document/add")
def add_document_api(req: DocumentAddRequest):
    """
    문서 추가 → VectorDB에 저장
    """
    add_document(req.file_path, req.target, req.security_level)
    return {"status": "Document added successfully"}

@app.get("/document/list")
def list_documents_api(target: str):
    """
    VectorDB 내 문서 리스트 조회
    """
    docs = list_documents(target)
    return {"documents": docs}

@app.delete("/document/delete")
def delete_document_api(req: DocumentDeleteRequest):
    """
    VectorDB 내 문서 삭제
    """
    delete_document(req.target, req.doc_source_name)
    return {"status": "Document deleted successfully"}
