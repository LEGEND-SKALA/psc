import { useNavigate } from 'react-router-dom'
import styled from 'styled-components'
import { FaQuestion } from 'react-icons/fa'
import { IoMicSharp, IoSend } from 'react-icons/io5'

const ChatInput = () => {
  const navigate = useNavigate()
  const currentPage = window.location.pathname

  return (
    <InputSection
      $currentPage={currentPage}
      onClick={() => document.querySelector('input').focus()}
    >
      <H2>질문하기</H2>
      <Input type="text" placeholder="무엇이든 물어보세요" />
      <Btns>
        <QuestionBtn>
          <FaQuestion color="#fff" size={15} />
        </QuestionBtn>
        <RightBtns>
          <VoiceBtn>
            <IoMicSharp color="#5c5c5c" size={25} />
          </VoiceBtn>
          <SubmitBtn type="submit" onClick={() => navigate('/chat')}>
            <IoSend color="#fff" size={14} />
          </SubmitBtn>
        </RightBtns>
      </Btns>
    </InputSection>
  )
}
export default ChatInput

const H2 = styled.h2`
  display: none;
`
const InputSection = styled.section`
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
  border-radius: 25px;
  width: ${(props) =>
    props.$currentPage == '/chat' ? 'calc(100vw - 40px)' : '60vw'};
  max-width: ${(props) =>
    props.$currentPage == '/chat' ? 'calc(60rem - 2.5rem)' : '38rem'};
  padding: 1.5rem 1.5rem 1rem;
  box-sizing: border-box;
  margin: 0 20px;
`
const Input = styled.input`
  margin-bottom: 1rem;
  border: none;
  width: 100%;
  font-size: 0.9rem;
  height: 1.1rem;

  &:focus {
    outline: none;
  }
`
const Btns = styled.div`
  display: flex;
`
const RightBtns = styled.div`
  margin-left: auto;
  display: flex;
`
const Button = styled.button`
  border: none;
  border-radius: 100%;
  width: 2rem;
  height: 2rem;
  background-color: #5c5c5c;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
`
const QuestionBtn = styled(Button)``
const VoiceBtn = styled(Button)`
  margin-right: 1rem;
  background-color: #fff;
  box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.25);
`
const SubmitBtn = styled(Button)``
