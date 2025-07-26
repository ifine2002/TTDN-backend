package vn.ifine.exception;

public class ResourceNotFoundException extends RuntimeException {
  // Constructor that accepts a message
  public ResourceNotFoundException(String message) {
    super(message);
  }
}