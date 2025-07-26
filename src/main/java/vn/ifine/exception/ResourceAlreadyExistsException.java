
package vn.ifine.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

  // Constructor that accepts a message
  public ResourceAlreadyExistsException(String message) {
    super(message);
  }
}
