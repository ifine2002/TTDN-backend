package vn.ifine.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResultPaginationDTO {
  private int page;
  private int pageSize;
  private int totalPages;
  private long totalElements;
  private Object result;
}
