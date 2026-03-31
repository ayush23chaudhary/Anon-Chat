import { motion } from 'framer-motion'
import { MessageSquare, Users, Lock, Plus } from 'lucide-react'

export default function Sidebar({ user, onLogout, onBackToDashboard, room }) {
  const chats = room ? [
    { id: room.id, name: room.name, unread: 0, online: true }
  ] : []

  const rooms = [
    { id: 1, name: 'Return to Dashboard', members: 'Explore' }
  ]

  return (
    <motion.div
      className="w-full md:w-80 bg-chat-bg border-r border-chat-bubbleOther flex flex-col"
      initial={{ x: -50, opacity: 0 }}
      animate={{ x: 0, opacity: 1 }}
      transition={{ duration: 0.5 }}
    >
      {/* User Profile */}
      <div className="p-6 border-b border-chat-bubbleOther">
        <div className="flex items-center gap-3 mb-4">
          <div className="w-12 h-12 rounded-lg bg-chat-primary flex items-center justify-center">
            <Lock className="w-6 h-6 text-chat-bg" />
          </div>
          <div className="flex-1">
            <p className="text-chat-textPrimary font-bold truncate">{user?.username || 'Anonymous'}</p>
            <p className="text-xs text-chat-textSecondary">
              ID: {user?.id?.substring(0, 8)}...
            </p>
          </div>
        </div>
        <motion.button
          onClick={onLogout}
          className="w-full py-2 text-sm text-chat-textSecondary hover:bg-chat-bubbleOther rounded-lg transition-colors"
          whileHover={{ scale: 1.02 }}
        >
          Logout
        </motion.button>
      </div>

      {/* Search */}
      <div className="p-4 border-b border-chat-bubbleOther">
        <input
          type="text"
          placeholder="Search chats..."
          className="w-full px-3 py-2 bg-chat-bubbleOther border border-chat-bubbleOther rounded-lg text-sm text-chat-textPrimary placeholder-chat-textSecondary focus:outline-none focus:border-chat-primary transition-colors"
        />
      </div>

      {/* Chats */}
      <div className="flex-1 overflow-y-auto">
        <div className="p-4">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-sm font-semibold text-chat-textSecondary uppercase">Messages</h3>
            <motion.button
              onClick={onBackToDashboard}
              whileHover={{ scale: 1.1 }}
              className="p-1 hover:bg-chat-bubbleOther rounded-lg transition-colors"
            >
              <Plus className="w-4 h-4 text-chat-primary" />
            </motion.button>
          </div>

          <div className="space-y-2">
            {chats.map((chat) => (
              <motion.div
                key={chat.id}
                className="p-3 bg-chat-bubbleOther rounded-lg cursor-pointer transition-colors relative border border-chat-primary/30"
                whileHover={{ x: 4 }}
              >
                <div className="flex items-start gap-3">
                  <div className="relative w-10 h-10 rounded-lg bg-chat-primary/20 flex items-center justify-center flex-shrink-0 text-chat-primary font-bold">
                    {chat.name.charAt(0).toUpperCase()}
                    <motion.div
                      className={`absolute bottom-0 right-0 w-3 h-3 rounded-full border-2 border-chat-bg ${
                        chat.online ? 'bg-chat-primary' : 'bg-chat-textSecondary'
                      }`}
                      animate={chat.online ? { boxShadow: ['0 0 0 0 rgba(139, 157, 255, 0.7)', '0 0 0 8px rgba(139, 157, 255, 0)'] } : {}}
                      transition={chat.online ? { duration: 2, repeat: Infinity } : {}}
                    />
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-chat-textPrimary font-semibold truncate">{chat.name}</p>
                    <p className="text-xs text-chat-textSecondary">Current Room...</p>
                  </div>
                  {chat.unread > 0 && (
                    <motion.span
                      className="text-xs bg-chat-primary text-chat-bg px-2 py-1 rounded-full font-bold flex-shrink-0"
                      animate={{ scale: [1, 1.1, 1] }}
                      transition={{ duration: 2, repeat: Infinity }}
                    >
                      {chat.unread}
                    </motion.span>
                  )}
                </div>
              </motion.div>
            ))}
          </div>
        </div>

        {/* Trending Rooms */}
        <div className="p-4 border-t border-chat-bubbleOther">
          <h3 className="text-sm font-semibold text-chat-textSecondary uppercase mb-4">Quick Links</h3>
          <div className="space-y-2">
            {rooms.map((rm) => (
              <motion.div
                key={rm.id}
                onClick={onBackToDashboard}
                className="p-3 hover:bg-chat-bubbleOther rounded-lg cursor-pointer transition-colors"
                whileHover={{ x: 4 }}
              >
                <p className="text-chat-textPrimary font-semibold truncate">{rm.name}</p>
                <p className="text-xs text-chat-textSecondary flex items-center gap-1">
                  <Users className="w-3 h-3" />
                  {rm.members}
                </p>
              </motion.div>
            ))}
          </div>
        </div>
      </div>

      {/* Footer */}
      <div className="p-4 border-t border-chat-bubbleOther">
        <div className="text-center text-xs text-chat-textSecondary">
          <p>🔐 All messages encrypted</p>
          <p className="mt-1 text-chat-primary">Signal Protocol v3</p>
        </div>
      </div>
    </motion.div>
  )
}
