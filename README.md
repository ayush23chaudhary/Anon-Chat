# AnonChat - Secure Messaging Platform

A production-grade, end-to-end encrypted messaging platform with Signal-style key exchange. The server acts as a relay and prekey store only—it never has access to message plaintext or user encryption keys.

## 🔐 Security Architecture

### Core Principles

1. **End-to-End Encryption (Client-Side Only)**
   - All message encryption/decryption happens on the client
   - Server NEVER decrypts messages
   - Encrypted messages are stored as-is on the server

2. **Signal-Style Key Exchange**
   - Users upload public prekeys and one-time prekeys
   - Server distributes these public keys for session establishment
   - Server has NO access to private keys

3. **JWT Authentication**
   - Stateless authentication using JWT tokens
   - Access tokens expire in 15 minutes
   - Refresh tokens expire in 7 days
   - Never log sensitive token information

4. **Secure Password Handling**
   - Passwords hashed with bcrypt
   - Never stored in plaintext
   - Never logged

## 📦 Project Structure

```
AnonChat/
├── common/                      # Shared utilities and DTOs
│   ├── exception/              # Custom exception classes
│   ├── constant/               # Security constants
│   └── dto/                    # Shared DTOs
│
├── auth-service/               # Authentication & JWT tokens
│   ├── config/                 # JwtTokenProvider
│   ├── service/                # AuthService
│   └── controller/             # AuthController
│
├── user-service/               # User management & registration
│   ├── entity/                 # User entity
│   ├── repository/             # UserRepository
│   ├── service/                # UserService
│   └── controller/             # UserController
│
├── prekey-service/             # Signal-style prekey management
│   ├── entity/                 # Prekey, OneTimePrekey entities
│   ├── repository/             # Prekey repositories
│   ├── service/                # PrekeyService
│   └── controller/             # PrekeyController
│
├── messaging-service/          # Message relay & WebSocket
│   ├── entity/                 # Message entity
│   ├── repository/             # MessageRepository
│   ├── service/                # MessagingService
│   ├── websocket/              # WebSocket handler & config
│   └── controller/             # MessagingController
│
├── queue-service/              # Offline message queue
│   ├── service/                # MessageQueueService
│   └── dto/                    # Queue DTOs
│
└── api-gateway/                # Main entry point & security
    ├── filter/                 # JWT authentication filter
    ├── config/                 # Security configuration
    ├── exception/              # Global exception handler
    └── resources/              # application.yml configuration
```

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 13+
- Redis 6+

### Database Setup

```bash
# Create database
createdb anonchat

# Create user
createuser anonchat_user
```

### Configuration

Set environment variables:

```bash
export DB_PASSWORD=your_secure_password
export REDIS_PASSWORD=your_redis_password
export JWT_SECRET=your_256_bit_secret_key_minimum_32_chars
export ADMIN_PASSWORD=your_admin_password
```

### Build & Run

```bash
# Build all modules
mvn clean install

# Run the API Gateway
mvn -pl api-gateway spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📚 API Endpoints

### Authentication

#### Register User
```
POST /api/v1/users/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "SecurePassword123!",
  "displayName": "Alice Smith"
}

Response: 201 Created
{
  "id": "uuid-here",
  "username": "alice",
  "email": "alice@example.com",
  "displayName": "Alice Smith",
  "isActive": true,
  "createdAt": "2024-03-17T10:00:00Z",
  "updatedAt": "2024-03-17T10:00:00Z"
}
```

#### Login
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "SecurePassword123!"
}

Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

#### Refresh Token
```
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}

Response: 200 OK
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

### Prekey Management

#### Upload Prekey
```
POST /api/v1/prekeys/upload
Authorization: Bearer <accessToken>
X-User-Id: <userId>
Content-Type: application/json

{
  "prekeyId": 1,
  "publicKey": "base64_encoded_public_key",
  "signature": "base64_encoded_signature"
}

Response: 201 Created
```

#### Upload One-Time Prekey
```
POST /api/v1/prekeys/one-time/upload
Authorization: Bearer <accessToken>
X-User-Id: <userId>
Content-Type: application/json

{
  "otPrekeyId": 1,
  "publicKey": "base64_encoded_public_key"
}

Response: 201 Created
```

#### Get User Bundle
```
GET /api/v1/prekeys/{userId}
Authorization: Bearer <accessToken>

Response: 200 OK
{
  "userId": "target-user-uuid",
  "identityKey": "base64_encoded_identity_key",
  "signedPrekey": {
    "prekeyId": 1,
    "publicKey": "base64_encoded_public_key",
    "signature": "base64_encoded_signature"
  },
  "oneTimePrekey": {
    "prekeyId": 1,
    "publicKey": "base64_encoded_public_key"
  }
}
```

### Messaging

#### Send Message
```
POST /api/v1/messages/send
Authorization: Bearer <accessToken>
X-User-Id: <userId>
Content-Type: application/json

{
  "recipientId": "recipient-uuid",
  "encryptedContent": "base64_encoded_encrypted_message",
  "messageType": "text"
}

Response: 201 Created
{
  "id": "message-uuid",
  "senderId": "sender-uuid",
  "recipientId": "recipient-uuid",
  "encryptedContent": "base64_encoded_encrypted_message",
  "messageType": "text",
  "isDelivered": false,
  "deliveredAt": null,
  "createdAt": "2024-03-17T10:05:00Z"
}
```

#### Get Pending Messages
```
GET /api/v1/messages/pending
Authorization: Bearer <accessToken>
X-User-Id: <userId>

Response: 200 OK
[
  {
    "id": "message-uuid",
    "senderId": "sender-uuid",
    "recipientId": "recipient-uuid",
    "encryptedContent": "base64_encoded_encrypted_message",
    "messageType": "text",
    "isDelivered": false,
    "createdAt": "2024-03-17T10:05:00Z"
  }
]
```

#### Acknowledge Message
```
POST /api/v1/messages/{messageId}/acknowledge
Authorization: Bearer <accessToken>
X-User-Id: <userId>

Response: 200 OK
```

### WebSocket

Connect to WebSocket for real-time messaging:

```
ws://localhost:8080/ws/messages?userId=<userId>&token=<accessToken>
```

**Message Types:**

1. **Send Message**
```json
{
  "type": "MESSAGE",
  "recipientId": "recipient-uuid",
  "encryptedContent": "base64_encoded_encrypted_message",
  "messageType": "text"
}
```

2. **Delivery Acknowledgment**
```json
{
  "type": "DELIVERY_ACK",
  "messageId": "message-uuid"
}
```

3. **Status Request**
```json
{
  "type": "STATUS"
}
```

Response:
```json
{
  "type": "STATUS",
  "pending_message_count": 5
}
```

## 🔒 Security Considerations

### Never Do This ❌

- **Never store plaintext messages** - Always store encrypted data
- **Never decrypt messages on server** - Decryption happens only on client
- **Never log authentication credentials** - Use generic error messages
- **Never send private keys over network** - Generate keys client-side
- **Never store passwords in plaintext** - Always use strong hashing (bcrypt)
- **Never commit secrets** - Use environment variables
- **Never trust client-side validation alone** - Always validate on server

### Always Do This ✅

- **Use HTTPS/WSS in production** - Encrypt all network traffic
- **Validate all inputs** - Prevent injection attacks
- **Use strong JWT secrets** - Minimum 256 bits (32 characters)
- **Rotate secrets regularly** - Especially JWT signing keys
- **Monitor authentication attempts** - Log and rate-limit failed attempts
- **Use UUIDs for identifiers** - Avoid sequential IDs
- **Implement rate limiting** - Prevent brute force attacks
- **Use strong password requirements** - Minimum 12 characters
- **Implement CORS properly** - Restrict to trusted origins
- **Keep dependencies updated** - Monitor for security vulnerabilities

## 🔑 Encryption Flow

### Session Establishment

```
1. Alice requests Bob's prekey bundle
   GET /prekeys/{bobId}
   ↓
2. Server returns Bob's public keys
   - Identity Key
   - Signed Prekey (with signature)
   - One-Time Prekey (optional)
   ↓
3. Alice uses Bob's public keys to derive shared secret
   (This happens ONLY on Alice's client)
   ↓
4. Alice can now encrypt messages to Bob
```

### Message Delivery

```
1. Alice encrypts message with derived key
2. Alice sends encrypted message to server
   POST /messages/send
   {
     "recipientId": "bob-uuid",
     "encryptedContent": "base64_encrypted"
   }
   ↓
3. Server stores encrypted message (never decrypts)
4. If Bob is online: WebSocket delivery
   If Bob is offline: Queued in Redis (encrypted)
   ↓
5. Bob receives encrypted message
6. Bob decrypts using his private key (ONLY on Bob's client)
```

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(32) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    display_name VARCHAR(255),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    version BIGINT
);
```

### Messages Table
```sql
CREATE TABLE messages (
    id UUID PRIMARY KEY,
    sender_id UUID NOT NULL,
    recipient_id UUID NOT NULL,
    encrypted_content TEXT NOT NULL,
    message_type VARCHAR(32) NOT NULL,
    is_delivered BOOLEAN DEFAULT false,
    delivered_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    version BIGINT,
    FOREIGN KEY (sender_id) REFERENCES users(id),
    FOREIGN KEY (recipient_id) REFERENCES users(id)
);
```

### Prekeys Table
```sql
CREATE TABLE prekeys (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    prekey_id INTEGER NOT NULL,
    public_key VARCHAR(1024) NOT NULL,
    signature VARCHAR(256),
    is_used BOOLEAN DEFAULT false,
    claimed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    version BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific module tests
mvn -pl auth-service test

# Run with coverage
mvn clean test jacoco:report
```

## 📝 Logging Policy

All logs are debug-level or lower to prevent sensitive data leakage:

- ✅ User IDs, usernames (no PII)
- ✅ Operation outcomes (success/failure - generic)
- ❌ Passwords, tokens, encryption keys
- ❌ Message content
- ❌ Personal information

## 🚀 Production Deployment

### Prerequisites

- TLS/SSL certificates for HTTPS
- WSS (WebSocket Secure) configuration
- Proper CORS configuration
- Rate limiting middleware
- DDoS protection
- Database backups

### Environment Variables

```bash
export DB_PASSWORD=<strong_password>
export REDIS_PASSWORD=<strong_password>
export JWT_SECRET=<256_bit_key>
export ADMIN_PASSWORD=<strong_password>
export LOG_LEVEL=WARN
```

### Scaling Considerations

- Database connection pooling
- Redis cluster for queue service
- Load balancing for WebSocket connections
- Message queue (RabbitMQ, Kafka) for async processing
- CDN for static assets

## 📖 API Documentation

Full API documentation available via Swagger/OpenAPI (to be implemented).

## 🤝 Contributing

Please ensure all security requirements are met:
- No plaintext passwords
- No sensitive data logging
- All inputs validated
- All errors generic (no information leakage)

## 📄 License

Proprietary - AnonChat Platform

## 🆘 Support

For security issues, please contact: security@anonchat.example
