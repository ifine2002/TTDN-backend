package vn.ifine.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {
  private int status;
  private String error;

  // message có thể là String, hoặc arraylist
  private Object message;
  private T data;

  public static <T> ApiResponse<T> success(String message, T data) {
    return ApiResponse.<T>builder()
        .status(200)
        .message(message)
        .data(data)
        .build();
  }

  public static <T> ApiResponse<T> created(String message, T data) {
    return ApiResponse.<T>builder()
        .status(201)
        .message(message)
        .data(data)
        .build();
  }
}
