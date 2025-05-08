from langchain_community.vectorstores import FAISS
from config import VECTORDB_DIRS
from utils.embedder import embed_text

def search(target: str, query: str, k: int = 5):
    vectordb_dir = VECTORDB_DIRS[target]
    vectordb = FAISS.load_local(vectordb_dir, embed_text, allow_dangerous_deserialization=True)

    return vectordb.similarity_search(query, k=k)

def search_mmr(target: str, query: str, k: int = 5, fetch_k: int = 20):
    vectordb_dir = VECTORDB_DIRS[target]
    vectordb = FAISS.load_local(vectordb_dir, embed_text, allow_dangerous_deserialization=True)

    return vectordb.max_marginal_relevance_search(query, k=k, fetch_k=fetch_k)
