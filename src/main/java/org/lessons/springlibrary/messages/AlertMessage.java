package org.lessons.springlibrary.messages;

public class AlertMessage {

  private final AlertMessageType type;
  private final String message;

  public AlertMessage(AlertMessageType type, String message) {
    this.type = type;
    this.message = message;
  }

  public AlertMessageType getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }


}
