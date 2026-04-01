import { useState, useEffect, useRef } from 'react'
import { motion } from 'framer-motion'
import { useNavigate } from 'react-router-dom'
import { LogOut, Plus, Search, Send, Zap, Shield, ArrowLeft, AlertCircle, Users, Reply, X } from 'lucide-react'
import Sidebar from '../components/Sidebar'
import ChatWindow from '../components/ChatWindow'
import ChatService from '../services/ChatService'
import { getUserColor } from '../utils/userColorUtils.ts'
import { getUserNickname, getNicknameEmoji } from '../utils/userAvatarUtils.ts'
import { UserAvatar } from '../components/UserAvatar'

export default function Chat({ user, token, room, onLogout }) {
  const [selectedChat, setSelectedChat] = useState(null)
  const [messages, setMessages] = useState([])
  const [inputValue, setInputValue] = useState('')
  const [isConnected, setIsConnected] = useState(false)
  const [memberCount, setMemberCount] = useState(room?.members || 1)
  const [replyTo, setReplyTo] = useState(null)
  const navigate = useNavigate()
  const messagesEndRef = useRef(null)

  // Initialize WebSocket connection when component mounts
  useEffect(() => {
    if (!room || !user) return

    const roomId = room.code || room.id
    const userId = user.id

    // Handle incoming messages
    const handleMessageReceived = (message) => {
      if (message.error) {
        console.error("Server error message:", message.error)
        return
      }

      // If it's a SYSTEM message update member count
      if (message.type === 'SYSTEM' && message.memberCount !== undefined) {
        setMemberCount(message.memberCount)
      }

      setMessages(prev => {
        let content = message.content
        const safeUserId = message.userId || 'System'
        if (message.type === 'SYSTEM') {
          // Improve system message text
          const friendlyName = (safeUserId === userId) ? "You" : `Anonymous (${safeUserId.substring(0,8)})`
          if (content === "User joined the room") {
            content = `${friendlyName} joined the room`
          } else if (content === "User left the room") {
            content = `${friendlyName} left the room`
          }
        }

        const formattedMessage = {
          id: message.id || (window.crypto.randomUUID ? window.crypto.randomUUID() : Date.now() + Math.random()),
          sender: safeUserId === userId ? 'me' : 'other',
          content: content,
          timestamp: message.timestamp ? new Date(message.timestamp) : new Date(),
          userId: safeUserId,
          type: message.type || 'USER',
          replyToText: message.replyToText // if any
        }

        // Prevent duplicate messages - check if message with same ID already exists
        const messageExists = prev.some(msg => msg.id === formattedMessage.id)
        if (messageExists) {
          return prev
        }

        return [...prev, formattedMessage]
      })
    }

    // Handle connection status changes
    const handleConnectionChange = (connected) => {
      setIsConnected(connected)
      if (!connected) {
        console.log('Connection lost, attempting to reconnect...')
      }
    }

    // Connect to WebSocket
    ChatService.connect(roomId, userId, handleMessageReceived, handleConnectionChange)

    // Auto-scroll to bottom when new messages arrive
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })

    // Cleanup on unmount
    return () => {
      ChatService.disconnect()
    }
  }, [room?.id, room?.code, user?.id])

  // Auto-scroll when messages change
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  const handleSendMessage = (e) => {
    e.preventDefault()
    if (!inputValue.trim() || !isConnected) return

    // Send via WebSocket
    const sent = ChatService.sendMessage(inputValue, replyTo?.content)
    
    if (sent) {
      // The message will be added to the chat when the server broadcasts it back
      setInputValue('')
      setReplyTo(null)
    } else {
      alert('Failed to send message. Connection may be lost.')
    }
  }

  const handleLogout = () => {
    onLogout()
    navigate('/')
  }

  const handleBackToDashboard = () => {
    navigate('/dashboard')
  }

  return (
    <div className="h-screen bg-chat-bg text-chat-textPrimary flex flex-col md:flex-row">
      {/* Sidebar */}
      <Sidebar user={user} onLogout={handleLogout} onBackToDashboard={handleBackToDashboard} room={room} />

      {/* Main Chat Area */}
      <div className="flex-1 flex flex-col border-l border-chat-bubbleOther">
        {/* Top Bar */}
        <motion.div
          className="bg-chat-bg border-b border-chat-bubbleOther px-6 py-4 flex items-center justify-between"
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          <div className="flex items-center gap-4">
            <motion.button
              onClick={handleBackToDashboard}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.95 }}
              className="p-2 hover:bg-chat-bubbleOther rounded-lg transition-colors"
            >
              <ArrowLeft className="w-5 h-5 text-chat-textSecondary" />
            </motion.button>
            <div>
              <h2 className="text-chat-textPrimary font-bold">{room?.name || 'General Chat'}</h2>
              <div className="flex items-center gap-2 text-sm">
                <motion.div
                  className={`w-2 h-2 rounded-full ${isConnected ? 'bg-chat-primary' : 'bg-chat-accent'}`}
                  animate={{ boxShadow: [
                    `0 0 0 0 ${isConnected ? 'rgba(139, 157, 255, 0.7)' : 'rgba(252, 165, 165, 0.7)'}`,
                    `0 0 0 10px ${isConnected ? 'rgba(139, 157, 255, 0)' : 'rgba(252, 165, 165, 0)'}`
                  ] }}
                  transition={{ duration: 2, repeat: Infinity }}
                />
                <span className={isConnected ? 'text-chat-primary' : 'text-chat-accent'}>
                  {isConnected ? 'Connected' : 'Connecting...'}
                </span>
                <span className="text-chat-textSecondary">• <Users className="w-3 h-3 inline mr-1" />{memberCount}</span>
                <span className="text-chat-textSecondary">• Encrypted</span>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-3">
            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.95 }}
              className="p-2 hover:bg-chat-bubbleOther rounded-lg transition-colors"
            >
              <Shield className="w-5 h-5 text-chat-primary" />
            </motion.button>
            <motion.button
              onClick={handleLogout}
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.95 }}
              className="p-2 hover:bg-chat-bubbleOther rounded-lg transition-colors"
            >
              <LogOut className="w-5 h-5 text-chat-textSecondary" />
            </motion.button>
          </div>
        </motion.div>

        {/* Messages Area */}
        <div className="flex-1 overflow-y-auto p-6 space-y-4">
          {messages.map((msg, idx) => {
            // Get deterministic color and nickname for each user
            const userColor = msg.sender === 'me' ? null : getUserColor(msg.userId, 'dark');
            const nickname = msg.sender === 'me' ? 'You' : getUserNickname(msg.userId);
            const emoji = msg.sender === 'me' ? '👤' : getNicknameEmoji(nickname);
            
            return msg.type === 'SYSTEM' ? (
              <motion.div 
                key={msg.id} 
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                className="flex justify-center my-2"
              >
                <div className="bg-chat-bubbleOther/50 text-chat-textSecondary text-xs px-3 py-1 rounded-full border border-chat-bubbleSelf">
                  {msg.content}
                </div>
              </motion.div>
            ) : (
            <motion.div
              key={msg.id}
              className={`flex group ${msg.sender === 'me' ? 'justify-end' : 'justify-start'}`}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: idx * 0.05 }}
            >
              <div className={`relative flex items-end gap-2 ${msg.sender === 'me' ? 'flex-row-reverse' : 'flex-row'}`}>
                {/* Avatar for other users */}
                {msg.sender !== 'me' && (
                  <UserAvatar 
                    userId={msg.userId}
                    backgroundColor={userColor?.background}
                    size={32}
                    variant="emoji"
                    className="flex-shrink-0"
                  />
                )}

                <div className="flex flex-col gap-1">
                  {/* Username label with color-coded left border */}
                  {msg.sender !== 'me' && (
                    <div 
                      className="text-xs font-bold px-3 py-1 rounded-t-md text-white"
                      style={{
                        backgroundColor: `${userColor?.background}20`,
                        borderLeft: `3px solid ${userColor?.background}`,
                        color: '#FFFFFF',
                      }}
                    >
                      {emoji} {nickname.split(' ').slice(1).join(' ')}
                    </div>
                  )}

                  {/* Message bubble */}
                  <div
                    className={`max-w-xs md:max-w-md px-4 py-3 rounded-lg ${
                      msg.sender === 'me'
                        ? 'bg-chat-bubbleSelf text-chat-textPrimary rounded-br-none'
                        : 'bg-chat-bubbleOther text-chat-textPrimary rounded-bl-none'
                    }`}
                    style={msg.sender !== 'me' ? {
                      borderLeft: `4px solid ${userColor?.background}`,
                    } : {}}
                  >
                    {msg.replyToText && (
                      <div className="mb-2 p-2 rounded text-xs border-l-2 bg-black/20 border-chat-primary opacity-80 overflow-hidden text-ellipsis whitespace-nowrap text-chat-textPrimary">
                        {msg.replyToText}
                      </div>
                    )}
                    <p className="break-words">{msg.content}</p>
                    <div className="flex items-center justify-between gap-4 mt-1">
                      <p 
                        className="text-[10px] font-semibold"
                        style={msg.sender !== 'me' ? {
                          color: userColor?.background,
                        } : {
                          color: '#A0AEC0'
                        }}
                      >
                        {msg.sender === 'me' ? 'You' : ''}
                      </p>
                      <p className="text-[10px] text-chat-textSecondary">
                        {msg.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                      </p>
                    </div>
                  </div>
                </div>

                <button 
                  onClick={() => setReplyTo(msg)}
                  className="opacity-0 group-hover:opacity-100 p-2 text-gray-400 hover:text-chat-primary transition-all"
                  title="Reply"
                >
                  <Reply className="w-4 h-4" />
                </button>
              </div>
            </motion.div>
            );
          })}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Area */}
        <motion.div
          className="bg-chat-bg border-t border-chat-bubbleOther px-6 py-4 flex flex-col"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
        >
          {replyTo && (
            <div className="flex items-center justify-between bg-chat-bubbleOther p-2 px-4 rounded-t-lg border-l-2 border-chat-primary text-sm mb-2 max-w-full overflow-hidden">
              <div className="flex flex-col overflow-hidden whitespace-nowrap">
                <span className="text-chat-primary text-xs font-bold">
                  Replying to {replyTo.sender === 'me' ? 'Yourself' : getUserNickname(replyTo.userId)}
                </span>
                <span className="text-chat-textSecondary text-xs truncate max-w-xs md:max-w-md">{replyTo.content}</span>
              </div>
              <button onClick={() => setReplyTo(null)} className="text-chat-textSecondary hover:text-chat-accent">
                <X className="w-4 h-4" />
              </button>
            </div>
          )}

          {!isConnected && (
            <motion.div
              className="mb-4 flex items-center gap-2 p-3 bg-chat-accent/20 border border-chat-accent/50 rounded-lg"
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
            >
              <AlertCircle className="w-4 h-4 text-chat-accent" />
              <p className="text-sm text-chat-accent">Connecting to chat room...</p>
            </motion.div>
          )}
          <form onSubmit={handleSendMessage} className="flex gap-3">
            <input
              type="text"
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              placeholder={isConnected ? "Type encrypted message..." : "Connecting..."}
              disabled={!isConnected}
              className="flex-1 px-4 py-3 bg-chat-bubbleOther border border-chat-bubbleSelf rounded-lg text-chat-textPrimary placeholder-chat-textSecondary focus:outline-none focus:border-chat-primary transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            />
            <motion.button
              type="submit"
              disabled={!inputValue.trim() || !isConnected}
              className="px-6 py-3 bg-chat-primary text-chat-bg rounded-lg font-semibold hover:shadow-lg hover:shadow-chat-primary/50 transition-all disabled:opacity-50 disabled:cursor-not-allowed"
              whileHover={{ scale: isConnected ? 1.05 : 1 }}
              whileTap={{ scale: isConnected ? 0.95 : 1 }}
            >
              <Send className="w-5 h-5" />
            </motion.button>
          </form>
        </motion.div>
      </div>
    </div>
  )
}
