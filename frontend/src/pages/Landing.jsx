import { motion } from 'framer-motion'
import { Lock, MessageSquare, Zap } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

export default function Landing() {
  const navigate = useNavigate()

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.2,
        delayChildren: 0.3,
      },
    },
  }

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.5 },
    },
  }

  return (
    <div className="min-h-screen bg-chat-bg overflow-hidden relative">
      {/* Animated Background */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <motion.div
          className="absolute w-96 h-96 bg-chat-accent rounded-full mix-blend-multiply filter blur-3xl opacity-10"
          animate={{ y: [0, -100, 0], x: [0, 100, 0] }}
          transition={{ duration: 20, repeat: Infinity }}
          style={{ top: '10%', left: '10%' }}
        />
        <motion.div
          className="absolute w-96 h-96 bg-chat-primary text-chat-bg rounded-full mix-blend-multiply filter blur-3xl opacity-10"
          animate={{ y: [0, 100, 0], x: [0, -100, 0] }}
          transition={{ duration: 25, repeat: Infinity, delay: 5 }}
          style={{ bottom: '10%', right: '10%' }}
        />
      </div>

      {/* Content */}
      <div className="relative z-10 h-screen flex flex-col items-center justify-center px-6">
        <motion.div
          className="text-center"
          variants={containerVariants}
          initial="hidden"
          animate="visible"
        >
          {/* Logo/Title */}
          <motion.div variants={itemVariants} className="mb-6">
            <div className="flex items-center justify-center gap-3 mb-4">
              <Lock className="w-12 h-12 text-chat-primary" />
              <h1 className="text-6xl font-bold text-chat-primary drop-shadow-md">AnonChat</h1>
            </div>
            <p className="text-2xl text-gray-300">Secure Encrypted Messaging</p>
          </motion.div>

          {/* Tagline */}
          <motion.p
            variants={itemVariants}
            className="text-gray-400 text-lg max-w-2xl mx-auto mb-12 leading-relaxed"
          >
            End-to-end encrypted messaging with Signal-style key exchange. Complete privacy,
            zero server access to your messages.
          </motion.p>

          {/* Features */}
          <motion.div
            variants={itemVariants}
            className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12 max-w-4xl mx-auto"
          >
            {[
              { icon: Lock, title: 'E2E Encrypted', desc: 'Your messages are encrypted' },
              { icon: Zap, title: 'Instant Delivery', desc: 'Real-time messaging' },
              { icon: MessageSquare, title: 'Anonymous', desc: 'No personal data required' },
            ].map((feature, idx) => (
              <motion.div
                key={idx}
                className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-6 rounded-xl hover:bg-opacity-20 transition-all"
                whileHover={{ y: -5, scale: 1.05 }}
              >
                <feature.icon className="w-8 h-8 text-chat-primary mx-auto mb-3" />
                <h3 className="text-white font-semibold mb-2">{feature.title}</h3>
                <p className="text-sm text-gray-400">{feature.desc}</p>
              </motion.div>
            ))}
          </motion.div>

          {/* CTA Button */}
          <motion.button
            variants={itemVariants}
            onClick={() => navigate('/auth')}
            className="px-8 py-4 bg-chat-primary text-chat-bg text-chat-bg font-bold rounded-lg text-lg hover:shadow-lg hover:shadow-chat-primary transition-all"
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            Start Secure Chat →
          </motion.button>
        </motion.div>
      </div>
    </div>
  )
}
