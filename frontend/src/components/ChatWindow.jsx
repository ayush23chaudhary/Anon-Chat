import { motion } from 'framer-motion'
import { Lock, Zap } from 'lucide-react'

export default function ChatWindow() {
  return (
    <motion.div
      className="flex-1 flex flex-col"
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ delay: 0.2 }}
    >
      <div className="flex-1 overflow-y-auto p-6 space-y-4">
        {/* Empty state or messages */}
        <div className="flex items-center justify-center h-full">
          <div className="text-center">
            <Lock className="w-16 h-16 text-neon-green mx-auto mb-4 opacity-50" />
            <p className="text-gray-400 text-lg">Select a chat to start messaging</p>
          </div>
        </div>
      </div>
    </motion.div>
  )
}
