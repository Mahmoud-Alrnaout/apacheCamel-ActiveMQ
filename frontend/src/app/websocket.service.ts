import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface MessageResponse {
  correlationId: string;
  data: {
    correlationId: string;
    content: string;
    status: string;
    timestamp?: string;
  };
  timestamp: number;
}

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket: WebSocket | null = null;
  private messagesSubject = new Subject<MessageResponse>();
  public messages$: Observable<MessageResponse> = this.messagesSubject.asObservable();
  private reconnectInterval = 5000;
  private reconnectTimer: any;

  constructor() {}

  connect(url: string = 'ws://localhost:8080/ws/messages'): void {
    if (this.socket?.readyState === WebSocket.OPEN) {
      console.log('WebSocket already connected');
      return;
    }

    try {
      this.socket = new WebSocket(url);

      this.socket.onopen = () => {
        console.log('WebSocket connected successfully');
        if (this.reconnectTimer) {
          clearTimeout(this.reconnectTimer);
          this.reconnectTimer = null;
        }
      };

      this.socket.onmessage = (event) => {
        try {
          const message: MessageResponse = JSON.parse(event.data);
          console.log('Received message:', message);
          this.messagesSubject.next(message);
        } catch (error) {
          console.error('Error parsing message:', error);
        }
      };

      this.socket.onerror = (error) => {
        console.error('WebSocket error:', error);
      };

      this.socket.onclose = () => {
        console.log('WebSocket disconnected. Attempting to reconnect...');
        this.reconnect(url);
      };
    } catch (error) {
      console.error('Error creating WebSocket:', error);
      this.reconnect(url);
    }
  }

  private reconnect(url: string): void {
    if (this.reconnectTimer) {
      return;
    }

    this.reconnectTimer = setTimeout(() => {
      console.log('Reconnecting to WebSocket...');
      this.reconnectTimer = null;
      this.connect(url);
    }, this.reconnectInterval);
  }

  disconnect(): void {
    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
  }

  isConnected(): boolean {
    return this.socket?.readyState === WebSocket.OPEN;
  }
}
