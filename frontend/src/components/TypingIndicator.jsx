import { motion } from 'framer-motion'
import { getUserColor } from '../utils/userColorUtils.ts'
import { getUserNickname, getNicknameEmoji } from '../utils/userAvatarUtils.ts'
import { UserAvatar } from './UserAvatar'

/**
 * TypingIndicator Component
 * Shows who is currently typing with animated dots
 * Multiple users are combined into one indicator
 */
export default function TypingIndicator({ typingUsers = [] }) {
  if (!typingUsers || typingUsers.length === 0) {
    return null
  }

  // Get first typing user info for display
  const firstTypingUser = typingUsers[0]
  const userColor = getUserColor(firstTypingUser, 'dark')
  const nickname = getUserNickname(firstTypingUser)
  const emoji = getNicknameEmoji(nickname)

  return (
    <motion.div
      className="flex items-end gap-2 mb-4"
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -10 }}
    >
      {/* Avatar */}
      <UserAvatar 
        userId={firstTypingUser}
        backgroundColor={userColor?.background}
        size={32}
        variant="emoji"
        className="flex-shrink-0"
      />

      <div className="flex flex-col gap-1">
        {/* Username label */}
        <div 
          className="text-xs font-bold px-3 py-1 rounded-t-md text-white"
          style={{
            backgroundColor: `${userColor?.background}20`,
            borderLeft: `3px solid ${userColor?.background}`,
            color: '#FFFFFF',
          }}
        >
          {emoji} {nickname.split(' ').slice(1).join(' ')}
          {typingUsers.length > 1 && ` +${typingUsers.length - 1}`}
        </div>

        {/* Typing bubble with animated dots */}
        <div
          className="bg-chat-bubbleOther text-chat-textPrimary rounded-lg rounded-bl-none px-4 py-3 flex items-center gap-1"
          style={{
            borderLeft: `4px solid ${userColor?.background}`,
          }}
        >
          <span className="text-xs text-chat-textSecondary">
            {typingUsers.length === 1 
              ? 'typing' 
              : `${typingUsers.length} people typing`}
          </span>
          
          {/* Animated dots */}
          <div className="flex gap-1 ml-1">
            {[0, 1, 2].map((i) => (
              <motion.span
                key={i}
                className="w-1.5 h-1.5 bg-chat-primary rounded-full"
                animate={{
                  y: [0, -6, 0],
                }}
                transition={{
                  duration: 0.6,
                  repeat: Infinity,
                  delay: i * 0.15,
                }}
              />
            ))}
          </div>
        </div>
      </div>
    </motion.div>
  )
}
