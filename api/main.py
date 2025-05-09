from fastapi import FastAPI, UploadFile, File, Form
from fastapi.middleware.cors import CORSMiddleware
import shutil
import os
from pathlib import Path

from utils.vectordb.document import add_document, delete_document
from agents.router_agent import route

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

RAW_DATA_DIR = "./data/raw"

@app.post("/document/add")
async def upload_document(
    file: UploadFile = File(...),
    category: str = Form(...),  # regulation or space
    security_level: str = Form("중")
):
    save_dir = os.path.join(RAW_DATA_DIR, category)
    os.makedirs(save_dir, exist_ok=True)
    save_path = os.path.join(save_dir, file.filename)

    # ✅ 중복 방지
    if os.path.exists(save_path):
        return {
            "message": f"❗ 이미 같은 이름의 파일({file.filename})이 존재합니다. 다른 이름으로 저장해주세요.",
            "success": False
        }

    # 저장
    with open(save_path, "wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    print(f"📥 벡터DB에 문서 추가 시도: {save_path}, category: {category}")

    # VectorDB에 추가
    add_document(save_path, category, security_level)

    return {
        "message": f"{file.filename} 업로드 및 벡터DB 등록 완료",
        "category": category,
        "success": True
    }

@app.post("/document/delete")
async def remove_document(
    doc_name: str = Form(...),
    category: str = Form(...)
):
    file_path = os.path.join(RAW_DATA_DIR, category, doc_name)
    file_deleted = False
    if os.path.exists(file_path):
        os.remove(file_path)
        file_deleted = True

    print(f"🗑 VectorDB에서 {doc_name} 제거 시도")
    delete_document(category, doc_name)

    return {
        "message": f"{doc_name} 삭제 완료",
        "file_deleted": file_deleted,
        "category": category
    }

@app.post("/chat")
async def chat(user_input: str = Form(...)):
    response = route(user_input)
    return {
        "user_input": user_input,
        "response": response
    }
