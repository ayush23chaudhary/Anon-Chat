import { useState } from 'react'
import { motion } from 'framer-motion'
import { Plus, Search, Lock, Users, Zap, X, Filter, Code } from 'lucide-react'

export default function Dashboard({ user, onRoomSelect }) {
  const [activeTab, setActiveTab] = useState('explore') // 'explore', 'joinCode', 'createFilter'
  const [joinCode, setJoinCode] = useState('')
  const [selectedFilters, setSelectedFilters] = useState({
    category: 'all',
    privacy: 'public',
    members: 'any'
  })
  const [searchQuery, setSearchQuery] = useState('')

  // Mock available rooms/channels
  const publicRooms = [
    { id: 1, name: 'General Discussion', members: 1247, topic: 'General', encrypted: true, icon: '💬' },
    { id: 2, name: 'Tech & Dev', members: 832, topic: 'Technology', encrypted: true, icon: '💻' },
    { id: 3, name: 'Gaming & Fun', members: 2104, topic: 'Entertainment', encrypted: true, icon: '🎮' },
    { id: 4, name: 'News & Politics', members: 456, topic: 'News', encrypted: true, icon: '📰' },
    { id: 5, name: 'Cryptocurrency', members: 1890, topic: 'Finance', encrypted: true, icon: '₿' },
    { id: 6, name: 'Privacy & Security', members: 654, topic: 'Security', encrypted: true, icon: '🔒' },
    { id: 7, name: 'Art & Design', members: 723, topic: 'Creative', encrypted: true, icon: '🎨' },
    { id: 8, name: 'Music & Sounds', members: 945, topic: 'Entertainment', encrypted: true, icon: '🎵' },
  ]

  const filteredRooms = publicRooms.filter(room => {
    const matchesSearch = room.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                         room.topic.toLowerCase().includes(searchQuery.toLowerCase())
    const matchesCategory = selectedFilters.category === 'all' || room.topic === selectedFilters.category
    return matchesSearch && matchesCategory
  })

  const handleJoinRoom = (room) => {
    onRoomSelect({ id: room.id, name: room.name })
  }

  const handleJoinWithCode = () => {
    if (joinCode.trim()) {
      onRoomSelect({ 
        id: `code-${joinCode}`, 
        name: `Private Room (${joinCode})`,
        code: joinCode
      })
      setJoinCode('')
    }
  }

  const handleCreateWithFilters = () => {
    // Generate a short 6 character room code
    const roomCode = Math.random().toString(36).substr(2, 6).toUpperCase()
    const roomName = `Room-${roomCode}`
    
    // Alert the user about their room code
    alert(`Room Created successfully!\n\nYour Room Code is: ${roomCode}\n\nShare this code with others so they can join your private room.`)
    
    onRoomSelect({ 
      id: `code-${roomCode}`, 
      name: roomName,
      code: roomCode,
      filters: selectedFilters 
    })
  }

  const categories = ['all', 'General', 'Technology', 'Entertainment', 'News', 'Finance', 'Security', 'Creative']

  return (
    <div className="min-h-screen bg-chat-bg pt-6">
      {/* Header */}
      <motion.div
        className="px-6 mb-8"
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
      >
        <div className="flex items-center justify-between mb-2">
          <h1 className="text-4xl font-bold bg-gradient-to-r from-chat-primary to-chat-accent bg-clip-text text-transparent">
            AnonChat
          </h1>
          <div className="px-4 py-2 bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md rounded-full">
            <p className="text-sm text-gray-300">ID: <span className="text-chat-primary font-mono">{user?.id}</span></p>
          </div>
        </div>
        <p className="text-gray-400">Welcome to anonymous encrypted chat</p>
      </motion.div>

      {/* Tab Navigation */}
      <motion.div
        className="px-6 mb-8 flex gap-3 overflow-x-auto pb-2"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 0.1 }}
      >
        {[
          { id: 'explore', label: 'Explore Rooms', icon: Search },
          { id: 'joinCode', label: 'Join with Code', icon: Code },
          { id: 'createFilter', label: 'Create Room', icon: Plus },
        ].map(tab => (
          <motion.button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`flex items-center gap-2 px-6 py-3 rounded-lg font-semibold transition-all whitespace-nowrap ${
              activeTab === tab.id
                ? 'bg-chat-primary text-chat-bg text-chat-bg shadow-lg shadow-chat-primary'
                : 'bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md text-gray-300 hover:text-white'
            }`}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
          >
            <tab.icon className="w-5 h-5" />
            {tab.label}
          </motion.button>
        ))}
      </motion.div>

      <div className="px-6 max-w-6xl mx-auto">
        {/* Tab: Explore Rooms */}
        {activeTab === 'explore' && (
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            {/* Search and Filters */}
            <div className="mb-8 space-y-4">
              <div className="relative">
                <Search className="absolute left-4 top-3.5 w-5 h-5 text-gray-400" />
                <input
                  type="text"
                  placeholder="Search rooms by name or topic..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="w-full pl-12 pr-4 py-3 bg-chat-bubbleOthers border border-chat-bubbleSelf rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-chat-primary transition-colors"
                />
              </div>

              {/* Category Filter */}
              <div className="flex gap-2 overflow-x-auto pb-2">
                {categories.map(cat => (
                  <motion.button
                    key={cat}
                    onClick={() => setSelectedFilters({ ...selectedFilters, category: cat })}
                    className={`px-4 py-2 rounded-lg font-semibold whitespace-nowrap transition-all ${
                      selectedFilters.category === cat
                        ? 'bg-chat-primary text-chat-bg text-chat-bg'
                        : 'bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md text-gray-300 hover:text-white'
                    }`}
                    whileHover={{ scale: 1.05 }}
                  >
                    {cat}
                  </motion.button>
                ))}
              </div>
            </div>

            {/* Rooms Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-12">
              {filteredRooms.map((room, idx) => (
                <motion.div
                  key={room.id}
                  className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-6 rounded-xl border border-chat-bubbleSelf hover:border-chat-primary cursor-pointer transition-all group"
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: idx * 0.05 }}
                  whileHover={{ translateY: -5 }}
                  onClick={() => handleJoinRoom(room)}
                >
                  <div className="flex items-start justify-between mb-3">
                    <span className="text-4xl">{room.icon}</span>
                    <Lock className="w-4 h-4 text-chat-primary" />
                  </div>
                  <h3 className="text-white font-bold mb-1 group-hover:text-chat-primary transition-colors">{room.name}</h3>
                  <p className="text-sm text-gray-400 mb-4">{room.topic}</p>
                  <div className="flex items-center gap-2 text-sm text-gray-500">
                    <Users className="w-4 h-4" />
                    {room.members.toLocaleString()} members
                  </div>
                </motion.div>
              ))}
            </div>
          </motion.div>
        )}

        {/* Tab: Join with Code */}
        {activeTab === 'joinCode' && (
          <motion.div
            className="max-w-md mx-auto"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <div className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-8 rounded-xl border border-chat-bubbleSelf">
              <h3 className="text-2xl font-bold text-white mb-2">Join Private Room</h3>
              <p className="text-gray-400 mb-6">Enter a room code to join a private encrypted room</p>

              <div className="space-y-4">
                <input
                  type="text"
                  placeholder="Enter room code"
                  value={joinCode}
                  onChange={(e) => setJoinCode(e.target.value.toUpperCase())}
                  className="w-full px-4 py-3 bg-chat-bubbleOthers border border-chat-bubbleSelf rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-chat-primary transition-colors font-mono text-center text-2xl tracking-widest"
                />

                <motion.button
                  onClick={handleJoinWithCode}
                  disabled={!joinCode.trim()}
                  className="w-full px-6 py-3 bg-chat-primary text-chat-bg text-chat-bg rounded-lg font-semibold hover:shadow-lg hover:shadow-chat-primary transition-all disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  <Code className="w-5 h-5" />
                  Join Room
                </motion.button>
              </div>

              <div className="mt-8 p-4 bg-chat-bubbleOthers rounded-lg border border-chat-bubbleSelf">
                <p className="text-sm text-gray-400">
                  💡 <span className="text-gray-300 ml-2">Room codes are 6-8 characters. Ask the room creator for the code.</span>
                </p>
              </div>
            </div>
          </motion.div>
        )}

        {/* Tab: Create Room with Filters */}
        {activeTab === 'createFilter' && (
          <motion.div
            className="max-w-2xl mx-auto"
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
          >
            <div className="bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md p-8 rounded-xl border border-chat-bubbleSelf">
              <h3 className="text-2xl font-bold text-white mb-2">Create New Room</h3>
              <p className="text-gray-400 mb-6">Set preferences for your private room</p>

              <div className="space-y-6">
                {/* Category */}
                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-3">Room Category</label>
                  <select
                    value={selectedFilters.category}
                    onChange={(e) => setSelectedFilters({ ...selectedFilters, category: e.target.value })}
                    className="w-full px-4 py-2 bg-chat-bubbleOthers border border-chat-bubbleSelf rounded-lg text-white focus:outline-none focus:border-chat-primary"
                  >
                    <option value="all">All Categories</option>
                    <option value="General">General</option>
                    <option value="Technology">Technology</option>
                    <option value="Entertainment">Entertainment</option>
                    <option value="News">News</option>
                    <option value="Finance">Finance</option>
                    <option value="Security">Security</option>
                    <option value="Creative">Creative</option>
                  </select>
                </div>

                {/* Privacy Level */}
                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-3">Privacy Level</label>
                  <div className="space-y-2">
                    {['public', 'private', 'secret'].map(level => (
                      <motion.label
                        key={level}
                        className="flex items-center p-3 bg-chat-bubbleOthers shadow-md border border-chat-bubbleSelf backdrop-blur-md rounded-lg cursor-pointer hover:bg-chat-bubbleSelf transition-colors"
                        whileHover={{ x: 5 }}
                      >
                        <input
                          type="radio"
                          name="privacy"
                          value={level}
                          checked={selectedFilters.privacy === level}
                          onChange={(e) => setSelectedFilters({ ...selectedFilters, privacy: e.target.value })}
                          className="mr-3"
                        />
                        <div>
                          <p className="font-semibold text-white capitalize">{level}</p>
                          <p className="text-xs text-gray-400">
                            {level === 'public' && 'Discoverable in public list'}
                            {level === 'private' && 'Invite-only with code'}
                            {level === 'secret' && 'Maximum privacy, no listing'}
                          </p>
                        </div>
                      </motion.label>
                    ))}
                  </div>
                </div>

                {/* Member Limit */}
                <div>
                  <label className="block text-sm font-semibold text-gray-300 mb-3">Max Members</label>
                  <select
                    value={selectedFilters.members}
                    onChange={(e) => setSelectedFilters({ ...selectedFilters, members: e.target.value })}
                    className="w-full px-4 py-2 bg-chat-bubbleOthers border border-chat-bubbleSelf rounded-lg text-white focus:outline-none focus:border-chat-primary"
                  >
                    <option value="any">No limit</option>
                    <option value="10">10 members</option>
                    <option value="50">50 members</option>
                    <option value="100">100 members</option>
                    <option value="500">500 members</option>
                  </select>
                </div>

                <motion.button
                  onClick={handleCreateWithFilters}
                  className="w-full px-6 py-3 bg-chat-primary text-chat-bg text-chat-bg rounded-lg font-semibold hover:shadow-lg hover:shadow-chat-primary transition-all flex items-center justify-center gap-2"
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  <Plus className="w-5 h-5" />
                  Create Room Now
                </motion.button>
              </div>

              <div className="mt-6 p-4 bg-chat-bubbleOthers rounded-lg border border-chat-bubbleSelf">
                <p className="text-sm text-gray-400">
                  🔐 <span className="text-gray-300 ml-2">All rooms are end-to-end encrypted by default</span>
                </p>
              </div>
            </div>
          </motion.div>
        )}
      </div>
    </div>
  )
}
