import { useEffect, useState } from 'react'
import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import { Lock, CheckCircle } from 'lucide-react'

export default function Auth({ onLogin }) {
  const [userId, setUserId] = useState(null)
  const [username, setUsername] = useState('')
  const navigate = useNavigate()

  // Auto-generate random user ID and redirect to chat
  useEffect(() => {
    const generateUser = () => {
      // Generate random user ID
      const randomId = 'user_' + Math.random().toString(36).substr(2, 9).toUpperCase()
      const randomUsername = 'Anon' + Math.floor(Math.random() * 10000)
      
      setUserId(randomId)
      setUsername(randomUsername)

      // Simulate a brief delay to show loading state
      setTimeout(() => {
        onLogin(
          { 
            id: randomId, 
            username: randomUsername 
          }, 
          'temp-token-' + randomId // Generate a temporary token
        )
        navigate('/dashboard')
      }, 2000)
    }

    generateUser()
  }, [onLogin, navigate])

  return (
    <div className="min-h-screen bg-chat-bg flex items-center justify-center px-6">
      {/* Background Elements */}
      <motion.div
        className="absolute w-96 h-96 bg-chat-accent rounded-full mix-blend-multiply filter blur-3xl opacity-10"
        animate={{ y: [0, -50, 0] }}
        transition={{ duration: 15, repeat: Infinity }}
        style={{ top: '20%', left: '10%' }}
      />

      <motion.div
        className="absolute w-96 h-96 bg-chat-primary rounded-full mix-blend-multiply filter blur-3xl opacity-10"
        animate={{ y: [0, 50, 0] }}
        transition={{ duration: 15, repeat: Infinity }}
        style={{ bottom: '20%', right: '10%' }}
      />

      {/* Loading Screen */}
      <motion.div
        className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-8 rounded-2xl w-full max-w-md text-center"
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5 }}
      >
        {/* Header */}
        <div className="mb-8">
          <div className="flex items-center justify-center gap-2 mb-4">
            <Lock className="w-8 h-8 text-chat-primary" />
            <h1 className="text-3xl font-bold text-chat-primary drop-shadow-md">AnonChat</h1>
          </div>
          <p className="text-gray-400">Generating Anonymous Identity...</p>
        </div>

        {/* Loading Animation */}
        <div className="space-y-6">
          {/* Spinner */}
          <motion.div
            className="w-16 h-16 mx-auto"
            animate={{ rotate: 360 }}
            transition={{ duration: 2, repeat: Infinity, ease: 'linear' }}
          >
            <div className="w-full h-full border-4 border-chat-bubbleSelf border-t-chat-primary rounded-full" />
          </motion.div>

          {/* Status Text */}
          {userId && (
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.5 }}
              className="space-y-2"
            >
              <div className="flex items-center justify-center gap-2 text-chat-primary">
                <CheckCircle className="w-5 h-5" />
                <span className="font-semibold">{username}</span>
              </div>
              <p className="text-gray-400 text-sm">ID: {userId}</p>
              <p className="text-gray-500 text-xs">Redirecting to chat...</p>
            </motion.div>
          )}
        </div>

        {/* Footer */}
        <p className="text-gray-500 text-xs mt-8">
          No account needed • Anonymous & Private
        </p>
      </motion.div>
    </div>
  )
}
