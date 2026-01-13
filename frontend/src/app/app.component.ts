import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessageService, MessageRequest, SendMessageResponse } from './message.service';
import { WebSocketService, MessageResponse } from './websocket.service';

interface DisplayMessage {
  id: string;
  sender: string;
  content: string;
  status: 'sending' | 'sent' | 'replied';
  timestamp: Date;
  response?: string;
  responseTimestamp?: Date;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  messageContent: string = '';
  senderName: string = '';
  messages: DisplayMessage[] = [];
  isConnected: boolean = false;

  constructor(
      private messageService: MessageService,
      private webSocketService: WebSocketService
  ) {}

  ngOnInit(): void {
    // Connect to WebSocket
    this.webSocketService.connect();

    // Check connection status
    setTimeout(() => {
      this.isConnected = this.webSocketService.isConnected();
    }, 1000);

    // Subscribe to WebSocket messages
    this.webSocketService.messages$.subscribe((response: MessageResponse) => {
      this.handleWebSocketMessage(response);
    });
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }

  sendMessage(): void {
    if (!this.messageContent.trim() || !this.senderName.trim()) {
      alert('Please fill in both sender name and message content');
      return;
    }

    const request: MessageRequest = {
      content: this.messageContent,
      sender: this.senderName
    };

    this.messageService.sendMessage(request).subscribe({
      next: (response: SendMessageResponse) => {
        // Add message to display with 'sent' status
        const displayMessage: DisplayMessage = {
          id: response.correlationId,
          sender: this.senderName,
          content: this.messageContent,
          status: 'sent',
          timestamp: new Date()
        };

        this.messages.unshift(displayMessage);

        // Clear form
        this.messageContent = '';

        console.log('Message sent successfully:', response);
      },
      error: (error) => {
        console.error('Error sending message:', error);
        alert('Error sending message. Please try again.');
      }
    });
  }

  private handleWebSocketMessage(response: MessageResponse): void {
    console.log('🔔 WebSocket received:', response);
    console.log('📩 Response content:', response.data?.content);

    const messageIndex = this.messages.findIndex(m => m.id === response.correlationId);

    if (messageIndex !== -1) {
      // Create a NEW object to trigger change detection
      this.messages[messageIndex] = {
        ...this.messages[messageIndex],  // Copy all existing properties
        status: 'replied',
        response: response.data.content,  // <-- FIXED: Access nested content
        responseTimestamp: new Date(response.timestamp)
      };

      // Force array reference change to trigger Angular
      this.messages = [...this.messages];

      console.log('✅ Message updated at index:', messageIndex);
    } else {
      // If message not found, create a new entry
      const displayMessage: DisplayMessage = {
        id: response.correlationId,
        sender: 'Unknown',
        content: 'Original message not found',
        status: 'replied',
        timestamp: new Date(response.timestamp),
        response: response.data.content,  // <-- FIXED: Access nested content
        responseTimestamp: new Date(response.timestamp)
      };

      this.messages.unshift(displayMessage);
      console.log('⚠️ Original message not found, created new entry');
    }
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'sending':
        return 'status-sending';
      case 'sent':
        return 'status-sent';
      case 'replied':
        return 'status-replied';
      default:
        return '';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'sending':
        return 'Sending...';
      case 'sent':
        return 'Waiting for reply...';
      case 'replied':
        return 'Replied';
      default:
        return '';
    }
  }
}
