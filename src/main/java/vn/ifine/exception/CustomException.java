package vn.ifine.exception;

public class CustomException extends RuntimeException {

  // Constructor that accepts a message
  public CustomException(String message) {
    super(message);
  }
}
