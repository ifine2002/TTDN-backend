package vn.ifine.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse<T> implements Serializable {
  private Date timestamp;
  private int status;
  private String path;
  private String error;
  private T message;

}
