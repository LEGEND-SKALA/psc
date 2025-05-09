from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse
from pathlib import Path
import shutil
import os

from utils.vectordb.document import add_document, delete_document
from config import DATA_DIRS
from agents.router_agent import route

app = FastAPI()


@app.post("/documents/{category}")
async def upload_pdf(category: str, file: UploadFile = File(...)):
    """
    PDF 파일 업로드 및 VectorDB에 추가
    """
    if category not in DATA_DIRS:
        raise HTTPException(status_code=400, detail="Invalid category")

    save_dir = Path(DATA_DIRS[category]["raw"])
    save_dir.mkdir(parents=True, exist_ok=True)
    save_path = save_dir / file.filename

    with save_path.open("wb") as buffer:
        shutil.copyfileobj(file.file, buffer)

    try:
        add_document(str(save_path), category)
        return JSONResponse(content={"message": f"✅ {file.filename} 업로드 및 추가 완료"}, status_code=200)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.delete("/documents/{category}/{filename}")
async def delete_pdf(category: str, filename: str):
    """
    PDF 파일 삭제 및 VectorDB에서 제거
    """
    if category not in DATA_DIRS:
        raise HTTPException(status_code=400, detail="Invalid category")

    file_path = Path(DATA_DIRS[category]["raw"]) / filename

    if not file_path.exists():
        raise HTTPException(status_code=404, detail="파일이 존재하지 않습니다")

    try:
        os.remove(file_path)
        delete_document(category, filename)
        return JSONResponse(content={"message": f"🗑️ {filename} 삭제 완료"}, status_code=200)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/chat")
async def chat_with_bot(user_input: str):
    """
    챗봇 질의 응답 API
    """
    try:
        response = route(user_input)
        return JSONResponse(content={"response": response}, status_code=200)
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
