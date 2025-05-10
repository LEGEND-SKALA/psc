# services/delete_document.py
import os
import shutil
from config import DATA_DIRS
from utils.vectordb.build_vectordb import build_vectordb

def delete_document(file_name: str, category: str):
    if category not in DATA_DIRS:
        raise ValueError("Category must be either 'regulation' or 'space'")

    raw_dir = DATA_DIRS[category]["raw"]
    file_path = os.path.join(raw_dir, file_name)

    if os.path.exists(file_path):
        os.remove(file_path)
        print(f"🗑️ PDF 삭제 완료: {file_path}")
    else:
        print(f"⚠️ PDF 파일이 존재하지 않습니다: {file_path}")

    # 관련 저장 데이터 모두 제거
    processed_dir = DATA_DIRS[category]["processed"]
    embed_dir = DATA_DIRS[category]["embeddings"]
    vectordb_dir = f"./vectordb/{category}_db"

    for dir_path in [processed_dir, embed_dir, vectordb_dir]:
        shutil.rmtree(dir_path, ignore_errors=True)
        os.makedirs(dir_path, exist_ok=True)

    print(f"🔁 VectorDB 및 연관 데이터 초기화 완료")

    # 남은 파일 기반 재구축
    print(f"🔄 {category} VectorDB 재구축 중...")
    build_vectordb(category)
    print(f"✅ 재구축 완료 (남은 문서 기준)")

if __name__ == "__main__":
    import sys
    delete_document(sys.argv[1], sys.argv[2])
