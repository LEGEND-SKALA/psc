import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import { AdminPage, ChatPage, LoginPage, MainPage } from './pages'
import './App.css'

function App() {
  console.log('Navi')
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/sign-up" element={<LoginPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/chat" element={<ChatPage />} />
        <Route path="/admin" element={<AdminPage />} />
        <Route path="*" element={<MainPage />} />
      </Routes>
    </Router>
  )
}

export default App
