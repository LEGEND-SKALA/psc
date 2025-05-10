import os
from fastapi import APIRouter, UploadFile, File, Form, HTTPException
from services.init_vectordb import initialize_vectordb
from services.add_document import add_document
from services.delete_document import delete_document
from services.multi_agent_chat import chat_with_agent

router = APIRouter()

@router.post("/init")
def init_vectordb():
    initialize_vectordb()
    return {"message": "✅ 모든 VectorDB 초기화 완료"}

@router.post("/add")
def add_pdf(file: UploadFile = File(...), category: str = Form(...)):
    if category not in ["regulation", "space"]:
        raise HTTPException(status_code=400, detail="category는 regulation 또는 space 여야 합니다.")

    temp_path = f"./temp/{file.filename}"
    os.makedirs(os.path.dirname(temp_path), exist_ok=True)
    with open(temp_path, "wb") as f:
        f.write(file.file.read())

    add_document(temp_path, category)
    os.remove(temp_path)

    return {"message": f"✅ {file.filename} 추가 완료", "category": category}

@router.delete("/delete")
def delete_pdf(filename: str, category: str):
    if category not in ["regulation", "space"]:
        raise HTTPException(status_code=400, detail="category는 regulation 또는 space 여야 합니다.")

    delete_document(filename, category)
    return {"message": f"🗑️ {filename} 삭제 및 VectorDB 재구축 완료", "category": category}

@router.post("/chat")
def chat(message_text: str = Form(...)):
    result = chat_with_agent(message_text)
    return result
