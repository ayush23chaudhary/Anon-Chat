import { motion } from 'framer-motion'
import { Shield, Lock, Zap } from 'lucide-react'
import { useNavigate } from 'react-router-dom'

export default function Privacy() {
  const navigate = useNavigate()

  return (
    <div className="min-h-screen bg-chat-bg px-6 py-12">
      <motion.div
        className="max-w-4xl mx-auto"
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        {/* Header */}
        <button
          onClick={() => navigate(-1)}
          className="mb-8 text-chat-primary hover:text-chat-accent transition-colors"
        >
          ← Back
        </button>

        <h1 className="text-5xl font-bold text-chat-primary drop-shadow-md mb-6">Security & Privacy</h1>
        <p className="text-gray-400 text-lg mb-12">
          Your privacy is our top priority. Here's how we protect your data.
        </p>

        {/* Security Features */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
          {[
            {
              icon: Lock,
              title: 'End-to-End Encryption',
              desc: 'All messages encrypted on your device. Server never has access to plaintext.',
            },
            {
              icon: Shield,
              title: 'Signal Protocol',
              desc: 'Industry-standard key exchange with perfect forward secrecy.',
            },
            {
              icon: Zap,
              title: 'Zero Knowledge',
              desc: 'We cannot read your messages, even if demanded. No master keys.',
            },
          ].map((feature, idx) => (
            <motion.div
              key={idx}
              className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-8 rounded-xl"
              whileHover={{ y: -5 }}
            >
              <feature.icon className="w-12 h-12 text-chat-primary mb-4" />
              <h3 className="text-white font-bold text-lg mb-3">{feature.title}</h3>
              <p className="text-gray-400">{feature.desc}</p>
            </motion.div>
          ))}
        </div>

        {/* Technical Details */}
        <motion.div
          className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-8 rounded-xl mb-12"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
        >
          <h2 className="text-2xl font-bold text-white mb-6">Technical Specifications</h2>
          <div className="space-y-4 text-gray-300">
            <div>
              <p className="font-semibold text-chat-primary">Encryption Algorithm</p>
              <p>Signal Protocol (Double Ratchet Algorithm)</p>
            </div>
            <div>
              <p className="font-semibold text-chat-primary">Key Size</p>
              <p>256-bit elliptic curve (X25519)</p>
            </div>
            <div>
              <p className="font-semibold text-chat-primary">Authentication</p>
              <p>JWT tokens with 1-hour expiration</p>
            </div>
            <div>
              <p className="font-semibold text-chat-primary">Transport</p>
              <p>TLS 1.3 over HTTPS/WSS</p>
            </div>
          </div>
        </motion.div>

        {/* CTA */}
        <motion.button
          onClick={() => navigate('/chat')}
          className="px-8 py-4 bg-chat-primary text-chat-bg text-chat-bg font-bold rounded-lg hover:shadow-lg hover:shadow-chat-primary transition-all w-full md:w-auto"
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
        >
          Continue to Chat →
        </motion.button>
      </motion.div>
    </div>
  )
}
