import { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom'
import Landing from './pages/Landing'
import Auth from './pages/Auth'
import Dashboard from './pages/Dashboard'
import Chat from './pages/Chat'
import Privacy from './pages/Privacy'

function AppRoutes({ user, token, onLogin, onLogout }) {
  const navigate = useNavigate()
  const [selectedRoom, setSelectedRoom] = useState(null)

  const handleRoomSelect = (room) => {
    setSelectedRoom(room)
    navigate('/chat')
  }

  useEffect(() => {
    if (token) {
      const stored = localStorage.getItem('user')
      if (stored && !user) {
        // User already logged in, keep them logged in
      }
    }
  }, [token, user])

  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/auth" element={<Auth onLogin={onLogin} />} />
      <Route 
        path="/dashboard" 
        element={token ? <Dashboard user={user} onRoomSelect={handleRoomSelect} /> : <Auth onLogin={onLogin} />} 
      />
      <Route 
        path="/chat" 
        element={token ? <Chat user={user} token={token} room={selectedRoom} onLogout={onLogout} onNavigateBack={() => navigate('/dashboard')} /> : <Auth onLogin={onLogin} />} 
      />
      <Route path="/privacy" element={<Privacy />} />
    </Routes>
  )
}

function App() {
  const [user, setUser] = useState(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [token, setToken] = useState(localStorage.getItem('token'))

  const handleLogin = (userData, authToken) => {
    setUser(userData)
    setToken(authToken)
    localStorage.setItem('token', authToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  const handleLogout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return (
    <Router>
      <AppRoutes 
        user={user} 
        token={token} 
        onLogin={handleLogin} 
        onLogout={handleLogout}
      />
    </Router>
  )
}

export default App
