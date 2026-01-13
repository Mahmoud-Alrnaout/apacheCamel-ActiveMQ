# ActiveMQ Camel Frontend

A simple Angular application for sending messages to the ActiveMQ Camel backend and receiving real-time responses via WebSocket.

## Features

- 📤 Send messages to ActiveMQ through REST API
- 🔄 Real-time message responses via WebSocket
- 🎯 Correlation ID tracking for request-response matching
- 📊 Message status tracking (Sending → Sent → Replied)
- 🔌 Auto-reconnecting WebSocket connection
- 💅 Beautiful, responsive UI

## Prerequisites

- Node.js (v18 or higher)
- npm (v9 or higher)
- Angular CLI (v17 or higher)

## Installation

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Install Angular CLI globally (if not already installed):
```bash
npm install -g @angular/cli
```

## Running the Application

### Start the Backend First

Make sure the Spring Boot backend is running on `http://localhost:8080` with ActiveMQ broker running.

### Start the Angular Development Server

```bash
npm start
```

Or:

```bash
ng serve
```

The application will be available at `http://localhost:4200`

## How It Works

### 1. Message Flow

```
User sends message → REST API (POST /api/messages/send)
                   ↓
            Receives correlationId
                   ↓
      Message sent to ActiveMQ queue
                   ↓
     External system processes message
                   ↓
   Response sent to response queue
                   ↓
      WebSocket broadcasts response
                   ↓
        UI updates automatically
```

### 2. WebSocket Connection

The application automatically connects to the WebSocket endpoint at `ws://localhost:8080/ws/messages` when the app loads:

- **Auto-connect**: Connects on application start
- **Auto-reconnect**: Automatically reconnects if connection is lost (5-second interval)
- **Status indicator**: Shows connection status in the header

### 3. Message Tracking

Each message is tracked with three states:

- **Sending**: Message is being sent to the server (not used currently, but available for loading states)
- **Sent**: Message successfully sent, waiting for response from ActiveMQ
- **Replied**: Response received and displayed

### 4. Components

#### WebSocket Service (`websocket.service.ts`)
- Manages WebSocket connection
- Handles reconnection logic
- Emits received messages via Observable

#### Message Service (`message.service.ts`)
- Handles HTTP requests to backend REST API
- Sends messages to `/api/messages/send` endpoint

#### App Component (`app.component.ts`)
- Main application component
- Manages message form and display
- Coordinates WebSocket and HTTP services

## Configuration

### Backend URLs

If your backend is running on a different host or port, update the URLs in:

**Message Service** (`src/app/message.service.ts`):
```typescript
private apiUrl = 'http://localhost:8080/api/messages';
```

**App Component** (`src/app/app.component.ts`):
```typescript
this.webSocketService.connect('ws://localhost:8080/ws/messages');
```

## Testing the Application

### Manual Testing

1. Start the backend (Spring Boot + ActiveMQ)
2. Start the Angular frontend (`npm start`)
3. Open browser at `http://localhost:4200`
4. Fill in your name and message content
5. Click "Send Message"
6. The message will appear with status "Waiting for reply..."
7. Send a response from ActiveMQ to the `responseMessage.queue` with the same correlationId
8. The UI will automatically update with the response

### Testing with ActiveMQ Console

1. Open ActiveMQ console at `http://localhost:8161`
2. Go to "Queues"
3. Find the `sendMessage.queue` and see your sent message
4. To simulate a response, send a message to `responseMessage.queue` with:
   - Header: `correlationId` = (the ID from your sent message)
   - Body: Your response text

## Building for Production

```bash
ng build --configuration production
```

The built files will be in the `dist/` directory.

## Project Structure

```
frontend/
├── src/
│   ├── app/
│   │   ├── app.component.ts          # Main component
│   │   ├── app.component.html        # Main template
│   │   ├── app.component.css         # Main styles
│   │   ├── websocket.service.ts      # WebSocket service
│   │   └── message.service.ts        # HTTP service
│   ├── index.html                    # HTML entry point
│   ├── main.ts                       # Application bootstrap
│   └── styles.css                    # Global styles
├── angular.json                      # Angular configuration
├── package.json                      # Dependencies
├── tsconfig.json                     # TypeScript configuration
└── README.md                         # This file
```

## Troubleshooting

### WebSocket Not Connecting

- Ensure backend is running on `http://localhost:8080`
- Check browser console for connection errors
- Verify CORS is enabled on backend (already configured with `@CrossOrigin(origins = "*")`)

### Messages Not Sending

- Check browser console for HTTP errors
- Verify backend REST API is accessible at `http://localhost:8080/api/messages/send`
- Ensure both "Sender Name" and "Message Content" fields are filled

### Responses Not Appearing

- Verify WebSocket connection is active (check status indicator in header)
- Ensure the correlationId in the response matches the sent message
- Check browser console for WebSocket message parsing errors

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

Same as the parent project.
