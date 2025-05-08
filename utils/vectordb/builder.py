import os
from pathlib import Path
from tqdm import tqdm
import numpy as np
import faiss
from langchain.schema import Document
from langchain_community.vectorstores import FAISS as LangChainFAISS
from langchain_community.docstore.in_memory import InMemoryDocstore
from utils.embedder import embed_text, image_to_embedding
from utils.pdf_loader import extract_text_chunks_from_pdf, extract_images_from_pdf
from config import DATA_DIRS, VECTORDB_DIRS

def build_vectordb(target: str):
    data_info = DATA_DIRS[target]
    vectordb_dir = VECTORDB_DIRS[target]

    os.makedirs(data_info["processed"], exist_ok=True)
    os.makedirs(data_info["embeddings"], exist_ok=True)
    os.makedirs(vectordb_dir, exist_ok=True)

    index = faiss.IndexFlatL2(384)
    docs = []

    files = list(Path(data_info["raw"]).glob("*.pdf"))
    if len(files) == 0:
        print(f"[경고] {data_info['raw']} 경로에 PDF 파일이 없습니다.")
        return

    for file in tqdm(files, desc=f"Processing {target} PDFs"):
        chunks = extract_text_chunks_from_pdf(str(file))

        for i, chunk in enumerate(chunks):
            Path(data_info["processed"], f"{file.stem}_chunk{i}.txt").write_text(chunk, encoding="utf-8")
            chunk_embedding = embed_text(chunk)
            np.save(Path(data_info["embeddings"], f"{file.stem}_chunk{i}.npy"), chunk_embedding)

            index.add(np.array([chunk_embedding], dtype=np.float32))
            docs.append(Document(page_content=chunk, metadata={"source": str(file), "chunk_index": i}))

        images = extract_images_from_pdf(str(file))
        for idx, img in enumerate(images):
            img_embedding = image_to_embedding(img)
            np.save(Path(data_info["embeddings"], f"{file.stem}_img{idx}.npy"), img_embedding)

            index.add(np.array([img_embedding], dtype=np.float32))
            docs.append(Document(page_content=f"Image from {file.stem} (index {idx})", metadata={"source": str(file), "type": f"image_{idx}"}))

    # 변경된 부분: index_to_docstore_id 인자 명시적으로 추가
    faiss.write_index(index, os.path.join(vectordb_dir, "index.faiss"))
    docstore = InMemoryDocstore({str(i): doc for i, doc in enumerate(docs)})
    lc_faiss = LangChainFAISS(embedding_function=embed_text,
                              index=index,
                              docstore=docstore,
                              index_to_docstore_id={i: str(i) for i in range(len(docs))})
    lc_faiss.save_local(vectordb_dir)

    print(f"✅ Saved VectorDB to {vectordb_dir}")
