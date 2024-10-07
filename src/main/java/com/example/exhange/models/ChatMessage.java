/* (C)2024 */
package com.example.exchange.model;

public class ChatMessage {
  private String content;
  private String sender;
  private MessageType type;

  public enum MessageType {
    CHAT,
    JOIN,
    LEAVE
  }

  public String getSender() {
    return sender;
  }
  // Getters and setters
}
