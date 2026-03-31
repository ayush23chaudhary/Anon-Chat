# AnonChat 🕵️‍♂️🔒

AnonChat is a fully anonymous, end-to-end encrypted real-time chat application built with Spring Boot microservices and React, featuring a modern Charcoal/Indigo theme.

## Architecture
- **Frontend**: React, Vite, Tailwind CSS, Frame Motion
- **Backend Gateway**: Spring Boot WebSockets, Spring Security
- **Microservices**: Auth, PreKey handling, Messaging queues
- **Persistence**: H2 (dev)/PostgreSQL & Redis (prod ready)

## Features
- Signal Protocol style E2E Encryption support
- Live WebSocket Chat Rooms
- Auto-Scroll, Reply, and Unread counts
- Secure room generation with 6-char hashes
- Beautiful Charcoal and Indigo UI

## Local Development
1. Start Gateway: `cd api-gateway && mvn spring-boot:run`
2. Start Frontend: `cd frontend && npm run dev`
