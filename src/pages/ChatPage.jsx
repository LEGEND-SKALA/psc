import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { ChatInput, ReviewModal } from '../components/chat'
import { Nav } from '../components/common'
import styled from 'styled-components'

const ChatPage = () => {
  const navigate = useNavigate()
  const [isOpen, setIsOpen] = useState(false)
  const handleOpen = () => {
    setIsOpen(true)
  }
  const handleClose = () => {
    setIsOpen(false)
    navigate('/')
  }
  return (
    <ChatPageContainer>
      <Nav modalOpen={handleOpen} />

      <ChatContainer>
        <ChatContent>
          <ChatDate>2025년 5월 8일 목요일</ChatDate>
          {chatList.map((item) => (
            <ChatItem
              key={item.id}
              content={item.content}
              role={item.role}
              date={item.date}
            />
          ))}
        </ChatContent>

        <ChatInput />

        <AlertComment>
          Navi는 실수를 할 수 있습니다. 중요한 정보는 재차 확인하세요.
        </AlertComment>

        <ReviewModal isOpen={isOpen} setIsOpen={handleClose} />
      </ChatContainer>
    </ChatPageContainer>
  )
}
export default ChatPage

const ChatItem = ({ content, role, date }) => {
  return (
    <ChatItemContainer $role={role}>
      {content.split('\n').map((line, index) => (
        <ChatItemContent key={index}>
          {line}
          {index !== content.split('\n').length - 1 && <br />}
        </ChatItemContent>
      ))}
    </ChatItemContainer>
  )
}

const chatList = [
  {
    id: 1,
    content: '조은정 매니저 님은 어디 계신가요?',
    role: 'user',
    date: '2023-10-01',
  },
  {
    id: 2,
    content:
      '동명이인이 존재해서 조은정 매니저 님이 속하신 부서를 말씀해주세요.',
    role: 'navi',
    date: '2023-10-01',
  },
  {
    id: 3,
    content: 'SKALA 교육 과정에 참여 중은 조은정 매니저 님을 찾고 있어요.',
    role: 'user',
    date: '2023-10-01',
  },
  {
    id: 4,
    content: 'SK C&C 사옥 내 8층 SKALA 2반에 계십니다.',
    role: 'navi',
    date: '2023-10-01',
  },
  {
    id: 5,
    content: '커피를 사드리려고 하는데, 사내에 카페가 있나요?',
    role: 'user',
    date: '2023-10-01',
  },
  {
    id: 6,
    content:
      'SK C&C 사옥 내 4층에 카페가 있어요.\n커피 뿐만 아니라 디저트, 주스 등 다양한 디저트를 판매하고 있어요.',
    role: 'navi',
    date: '2023-10-01',
  },
]

const ChatPageContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-width: 100vw;
  width: fit-content;
  height: 100vh;
`
const ChatContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: calc(100vh - 80px);
`
const AlertComment = styled.p`
  font-size: 0.8rem;
  margin: 1rem 0 1.5rem;
  box-sizing: border-box;
`

const ChatDate = styled.p`
  font-size: 0.8rem;
  font-weight: 700;
  margin: 0;
  justify-self: center;
`
const ChatContent = styled.div`
  flex: 1;
  width: 100vw;
  margin-bottom: 0.2rem;
  max-width: 60rem;
  overflow-y: auto;
`
const ChatItemContainer = styled.div`
  display: flex;
  width: fit-content;
  max-width: 80%;
  flex-direction: column;
  padding: 0.8rem 1rem;
  margin: 0.5rem 1.25rem !important;
  margin-left: ${(props) =>
    props.$role === 'user' ? 'auto' : '1.25rem'} !important;
  background-color: ${(props) => (props.$role === 'user' ? '#F7F7F7' : '#fff')};
  border-radius: 25px;
  box-shadow: 0px 1px 2px rgba(0, 0, 0, 0.25);
`
const ChatItemContent = styled.p`
  font-size: 0.9rem;
  margin: 0;
`
