package vn.ifine.exception;

public class InvalidTokenException extends RuntimeException {

  // Constructor that accepts a message
  public InvalidTokenException(String message) {
    super(message);
  }
}
