import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import { LoginPage, MainPage } from './pages'
import './App.css'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/sign-up" element={<LoginPage />} />
        <Route path="/" element={<MainPage />} />
      </Routes>
    </Router>
  )
}

export default App
