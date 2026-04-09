import { motion } from 'framer-motion'
import { getUserNickname, getNicknameEmoji } from '../utils/userAvatarUtils.ts'

/**
 * TypingIndicator Component
 * Shows who is currently typing with animated dots
 * Multiple users are combined into one indicator
 */
export default function TypingIndicator({ typingUsers = [] }) {
  if (!typingUsers || typingUsers.length === 0) {
    return null
  }

  return (
    <motion.div
      className="flex items-center gap-1 mb-4 px-3 py-2"
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -10 }}
    >
      {/* Show emojis for all typing users */}
      <div className="flex gap-1 items-center">
        {typingUsers.map((userId, index) => {
          const nickname = getUserNickname(userId)
          const emoji = getNicknameEmoji(nickname)
          
          return (
            <motion.div
              key={userId}
              className="flex items-center justify-center w-6 h-6 text-lg"
              animate={{ scale: [1, 1.1, 1] }}
              transition={{
                duration: 0.6,
                repeat: Infinity,
                delay: index * 0.15,
              }}
              title={nickname}
            >
              {emoji}
            </motion.div>
          )
        })}
      </div>

      {/* Animated typing indicator dots */}
      <div className="flex gap-1 ml-2">
        {[0, 1, 2].map((i) => (
          <motion.span
            key={i}
            className="w-1 h-1 bg-chat-textSecondary rounded-full"
            animate={{
              y: [0, -4, 0],
              opacity: [0.6, 1, 0.6],
            }}
            transition={{
              duration: 0.6,
              repeat: Infinity,
              delay: i * 0.15,
            }}
          />
        ))}
      </div>
    </motion.div>
  )
}
