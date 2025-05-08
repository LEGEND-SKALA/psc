import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import { LoginPage } from './pages'
import './App.css'

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/sign-up" element={<LoginPage />} />
      </Routes>
    </Router>
  )
}

export default App
