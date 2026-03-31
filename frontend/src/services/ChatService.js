/**
 * ChatService - WebSocket-based real-time chat service
 * Manages connections to chat rooms and message synchronization
 */

// Environment variables for backend API and WebSocket URLs
// Defaults to localhost for development, overridden by Vite build-time env vars for production
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081/api'
const WS_BASE_URL = import.meta.env.VITE_WS_URL || 'ws://localhost:8081/api/ws/chat'

class ChatService {
  constructor() {
    this.ws = null
    this.roomId = null
    this.userId = null
    this.messageCallback = null
    this.connectionCallback = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = 5
    this.reconnectDelay = 3000
  }

  /**
   * Connect to a chat room via WebSocket
   */
  connect(roomId, userId, onMessageReceived, onConnectionChange) {
    this.roomId = roomId
    this.userId = userId
    this.messageCallback = onMessageReceived
    this.connectionCallback = onConnectionChange

    try {
      // Build WebSocket URL using environment variable
      // WS_BASE_URL is set by Vite (import.meta.env.VITE_WS_URL)
      // For development: ws://localhost:8081/api/ws/chat
      // For production: wss://backend-domain.koyeb.app/api/ws/chat (from Vercel env vars)
      const wsUrl = `${WS_BASE_URL}/${roomId}?userId=${userId}`
      
      console.log('Connecting to WebSocket:', wsUrl)
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        console.log('WebSocket connected to room:', roomId)
        this.reconnectAttempts = 0
        if (this.connectionCallback) {
          this.connectionCallback(true)
        }
      }

      this.ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          console.log('Message received:', message)
          if (this.messageCallback) {
            this.messageCallback(message)
          }
        } catch (error) {
          console.error('Error parsing message:', error)
        }
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error)
        if (this.connectionCallback) {
          this.connectionCallback(false)
        }
      }

      this.ws.onclose = () => {
        console.log('WebSocket disconnected')
        if (this.connectionCallback) {
          this.connectionCallback(false)
        }
        this.attemptReconnect()
      }
    } catch (error) {
      console.error('Failed to connect WebSocket:', error)
      this.attemptReconnect()
    }
  }

  /**
   * Send a message to the room
   */
  sendMessage(content, replyToText = null) {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket not connected')
      return false
    }

    const message = {
      roomId: this.roomId,
      userId: this.userId,
      content: content,
      timestamp: new Date().toISOString(),
      type: 'USER',
      replyToText: replyToText
    }

    try {
      this.ws.send(JSON.stringify(message))
      return true
    } catch (error) {
      console.error('Failed to send message:', error)
      return false
    }
  }

  /**
   * Attempt to reconnect to WebSocket
   */
  attemptReconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`)
      setTimeout(() => {
        this.connect(this.roomId, this.userId, this.messageCallback, this.connectionCallback)
      }, this.reconnectDelay)
    } else {
      console.error('Max reconnection attempts reached')
    }
  }

  /**
   * Disconnect from the room
   */
  disconnect() {
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
  }

  /**
   * Check if connected
   */
  isConnected() {
    return this.ws && this.ws.readyState === WebSocket.OPEN
  }
}

export default new ChatService()
