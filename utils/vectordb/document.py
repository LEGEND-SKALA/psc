import os
import shutil
import numpy as np
from pathlib import Path
from langchain_community.vectorstores import FAISS as LangChainFAISS
from langchain_community.docstore.in_memory import InMemoryDocstore
from langchain.schema import Document
import faiss
from utils.embedder import embed_text, image_to_embedding
from utils.pdf_loader import extract_text_chunks_from_pdf, extract_images_from_pdf
from config import VECTORDB_DIRS

def add_document(pdf_path: str, target: str, security_level: str = "중"):
    from langchain_community.docstore.in_memory import InMemoryDocstore
    import faiss

    vectordb_dir = VECTORDB_DIRS[target]

    # ✅ 예외 발생 시 빈 DB 생성
    try:
        vectordb = LangChainFAISS.load_local(vectordb_dir, embed_text, allow_dangerous_deserialization=True)
    except Exception as e:
        print(f"❗ 기존 벡터DB 로드 실패. 새로 생성합니다: {e}")
        index = faiss.IndexFlatL2(384)
        vectordb = LangChainFAISS(index=index, docstore=InMemoryDocstore({}), index_to_docstore_id={}, embedding_function=embed_text)

    print(f"📌 [add_document] 시작 - 경로: {pdf_path}, 카테고리: {target}, 보안등급: {security_level}")

    chunks = extract_text_chunks_from_pdf(pdf_path)
    for i, chunk in enumerate(chunks):
        chunk_embedding = embed_text(chunk)
        doc = Document(page_content=chunk, metadata={"source": pdf_path, "chunk_index": i, "security_level": security_level})
        vectordb.index.add(np.array([chunk_embedding], dtype=np.float32))
        vectordb.docstore._dict[str(len(vectordb.docstore._dict))] = doc

    images = extract_images_from_pdf(pdf_path)
    for idx, img in enumerate(images):
        img_embedding = image_to_embedding(img)
        doc = Document(page_content=f"Image from {pdf_path} (index {idx})", metadata={"source": pdf_path, "type": f"image_{idx}", "security_level": security_level})
        vectordb.index.add(np.array([img_embedding], dtype=np.float32))
        vectordb.docstore._dict[str(len(vectordb.docstore._dict))] = doc

    vectordb.save_local(vectordb_dir)
    print(f"✅ 문서 추가 및 저장 완료: {vectordb_dir}")


def list_documents(target: str):
    vectordb_dir = VECTORDB_DIRS[target]
    vectordb = LangChainFAISS.load_local(vectordb_dir, embed_text, allow_dangerous_deserialization=True)
    
    summary = {}
    for doc in vectordb.docstore._dict.values():
        source = doc.metadata.get("source", "unknown")
        security_level = doc.metadata.get("security_level", "중")
        summary[Path(source).name] = security_level

    return list(summary.items())

def delete_document(target: str, doc_source_name: str):
    vectordb_dir = VECTORDB_DIRS[target]
    vectordb = LangChainFAISS.load_local(vectordb_dir, embed_text, allow_dangerous_deserialization=True)

    remaining_docs = []
    delete_docs = []

    # 삭제 대상과 남길 문서 구분
    for doc in vectordb.docstore._dict.values():
        source = doc.metadata.get("source", "")
        if doc_source_name in source:
            delete_docs.append(doc)
        else:
            remaining_docs.append(doc)

    if not delete_docs:
        print(f"❗ 삭제 대상 문서를 찾을 수 없습니다: {doc_source_name}")
        return

    # 새로운 Index 및 Docstore 생성
    index = faiss.IndexFlatL2(384)
    new_docstore = {}

    for doc in remaining_docs:
        doc_type = doc.metadata.get("type", "")
        if doc_type.startswith("image"):
            continue

        arr = embed_text(doc.page_content)
        index.add(np.array([arr], dtype=np.float32))
        new_docstore[str(index.ntotal - 1)] = doc

    # ✅ 여기서 index_to_docstore_id 추가
    index_to_docstore_id = {i: str(i) for i in range(len(new_docstore))}
    new_vectordb = LangChainFAISS(index, InMemoryDocstore(new_docstore), index_to_docstore_id)
    new_vectordb.save_local(vectordb_dir)

    print(f"✅ 삭제 및 재구성 완료: {doc_source_name}")
