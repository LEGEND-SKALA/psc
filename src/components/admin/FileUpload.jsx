import React, { useEffect, useRef, useState } from 'react'
import styled from 'styled-components'
import { FolderIcon } from '../../assets/common'
import axios from 'axios'

const FileUploader = () => {
  const fileInputRef = useRef(null)
  const [isDragging, setIsDragging] = useState(false)
  const [uploadedFile, setUploadedFile] = useState(null)

  useEffect(() => {
    if (uploadedFile) {
      handleUploadFile()
    }
  }, [uploadedFile])

  const handleUploadFile = () => {
    if (!uploadedFile) return

    const formData = new FormData()
    formData.append('file', uploadedFile)
    formData.append('category', 'SPACE')
    formData.append('security', 'MEDIUM')

    axios
      .post(`${process.env.REACT_APP_SERVER_URL}/documents`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: `Bearer ${localStorage.getItem('NaviToken')}`,
        },
      })
      .then((response) => {
        console.log(response.data)
        if (response.data.code === 0) {
          alert('파일 업로드 성공')
        } else {
          alert('파일 업로드 실패')
        }
      })
      .catch((error) => {
        console.error('파일 업로드 오류:', error)
        alert('파일 업로드 중 오류가 발생했습니다.')
      })
  }

  const handleDrop = (e) => {
    e.preventDefault()
    setIsDragging(false)
    const file = e.dataTransfer.files[0]
    if (file) setUploadedFile(file)
  }

  const handleFileChange = (e) => {
    const file = e.target.files?.[0]
    if (file) setUploadedFile(file)
  }

  return (
    <Wrapper>
      <DropArea
        onDragOver={(e) => {
          e.preventDefault()
          setIsDragging(true)
        }}
        onDragLeave={() => setIsDragging(false)}
        onDrop={handleDrop}
        isDragging={isDragging}
      >
        <FolderIconWrapper src={FolderIcon} alt="upload" />
        <UploadComment>파일을 드래그 해서</UploadComment>
        <UploadComment>업로드 하세요</UploadComment>

        <UploadButton onClick={() => fileInputRef.current?.click()}>
          파일 선택
        </UploadButton>

        <input
          type="file"
          ref={fileInputRef}
          style={{ display: 'none' }}
          onChange={handleFileChange}
        />
      </DropArea>
    </Wrapper>
  )
}
export default FileUploader

const Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  min-height: 0;
  width: 100%;
`

const DropArea = styled.div`
  border: 2px dashed #caccd4;
  border-color: ${({ isDragging }) => (isDragging ? '#bac1dd' : '#caccd4')};
  text-align: center;
  border-radius: 8px;
  background-color: ${({ isDragging }) => (isDragging ? '#eef2ff' : '#F9FAFD')};
  transition: background-color 0.2s;
  flex: 1;
  min-height: 0;
  width: 100%;
  align-content: center;
  overflow: auto;
`
const UploadButton = styled.button`
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: #4394ff;
  color: white;
  font-size: 0.9rem;
  font-weight: bold;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 1rem;

  &:hover {
    background-color: #3a7bff;
  }
  &:active {
    background-color: #2f6eff;
  }
`
const FolderIconWrapper = styled.img`
  width: 4rem;
  height: 4rem;
  margin-bottom: 1rem;
  padding-top: 1rem;
`
const UploadComment = styled.p`
  font-size: 0.8rem;
  margin: 0;
  margin-bottom: 0.3rem;
`
