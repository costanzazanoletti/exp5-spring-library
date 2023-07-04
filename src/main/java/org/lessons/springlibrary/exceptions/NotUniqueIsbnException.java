package org.lessons.springlibrary.exceptions;

public class NotUniqueIsbnException extends RuntimeException {

  public NotUniqueIsbnException(String message) {
    super(message);
  }
}
