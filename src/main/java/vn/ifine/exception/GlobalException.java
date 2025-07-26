package vn.ifine.exception;

import jakarta.validation.ConstraintViolationException;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
@Slf4j
public class GlobalException {

  // xử lý lỗi khi valid dữ liệu (@Valid)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(BAD_REQUEST)
  public ResponseEntity<ErrorResponse<Object>> validationError(MethodArgumentNotValidException ex, WebRequest request) {
    log.error("Exception caught: ", ex);  // Log toàn bộ stack trace
    BindingResult result = ex.getBindingResult();
    final List<FieldError> fieldErrors = result.getFieldErrors();
    List<String> errors = fieldErrors.stream().map(f -> f.getDefaultMessage()).toList();
    ErrorResponse<Object> errorResponse = ErrorResponse.builder()
        .timestamp(new Date())
        .status(BAD_REQUEST.value())
        .error(ex.getBody().getDetail())
        .message(errors.size() > 1 ? errors : errors.get(0))
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(value = { ResourceNotFoundException.class})
  @ResponseStatus(BAD_REQUEST)
  public ResponseEntity<ErrorResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
    log.error("Exception caught: ", ex);  // Log toàn bộ stack trace
    ErrorResponse<Object> errorResponse = ErrorResponse.builder()
        .timestamp(new Date())
        .status(BAD_REQUEST.value())
        .error(BAD_REQUEST.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(value = {ResourceAlreadyExistsException.class})
  @ResponseStatus(CONFLICT)
  public ResponseEntity<ErrorResponse<Object>> handleResourceAlreadyExistsException(ResourceAlreadyExistsException ex, WebRequest request) {
    log.error("Exception caught: ", ex);  // Log toàn bộ stack trace
    ErrorResponse<Object> errorResponse = ErrorResponse.builder()
        .timestamp(new Date())
        .status(CONFLICT.value())
        .error(CONFLICT.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  @ResponseStatus(BAD_REQUEST)
  public ResponseEntity<ErrorResponse<Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    log.error("Exception caught: ", ex);  // Log toàn bộ stack trace
    ErrorResponse<Object> errorResponse = ErrorResponse.builder()
        .timestamp(new Date())
        .status(BAD_REQUEST.value())
        .error(BAD_REQUEST.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse<Object>> handleAllUncaughtException(
      Exception ex,
      WebRequest request) {
    log.error("Unknown error occurred", ex);
    ErrorResponse<Object> errorResponse = ErrorResponse.builder()
        .timestamp(new Date())
        .status(INTERNAL_SERVER_ERROR.value())
        .error("Unknown error occurred")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
  }


}
