# services/add_document.py
import os
import shutil
from config import DATA_DIRS
from utils.vectordb.build_vectordb import build_vectordb

def add_document(pdf_path: str, category: str):
    if category not in DATA_DIRS:
        raise ValueError("Category must be either 'regulation' or 'space'")

    file_name = os.path.basename(pdf_path)
    raw_dir = DATA_DIRS[category]["raw"]
    os.makedirs(raw_dir, exist_ok=True)

    dest_path = os.path.join(raw_dir, file_name)
    shutil.copy2(pdf_path, dest_path)
    print(f"📥 PDF 저장 완료: {dest_path}")

    print(f"🔄 {category} VectorDB 재구축 중...")
    build_vectordb(category)  # 동일한 Chunking 방식 적용됨
    print(f"✅ VectorDB 업데이트 완료")

if __name__ == "__main__":
    import sys
    add_document(sys.argv[1], sys.argv[2])
