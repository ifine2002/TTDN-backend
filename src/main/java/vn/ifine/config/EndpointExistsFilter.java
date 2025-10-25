package vn.ifine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import vn.ifine.exception.ErrorResponse;

@Slf4j
@Component
public class EndpointExistsFilter extends OncePerRequestFilter {

  private final RequestMappingHandlerMapping handlerMapping;
  private final ObjectMapper objectMapper;

  public EndpointExistsFilter(RequestMappingHandlerMapping handlerMapping,
      ObjectMapper objectMapper) {
    this.handlerMapping = handlerMapping;
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    // Loại trừ các đường dẫn WebSocket và các static resources
    return path.startsWith("/ws");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String requestPath = request.getRequestURI();

    // Kiểm tra xem endpoint có tồn tại trong hệ thống không
    if (!isEndpointExists(request)) {
      log.warn("Endpoint not found: {} {}", request.getMethod(), requestPath);
      // Trả về 404 nếu endpoint không tồn tại
      sendNotFoundResponse(response, request);
      return;
    }

    // Endpoint tồn tại, tiếp tục chain
    filterChain.doFilter(request, response);
  }

  /**
   * Kiểm tra xem endpoint có được mapping trong hệ thống không
   */
  private boolean isEndpointExists(HttpServletRequest request) {
    try {
      HandlerExecutionChain handler = handlerMapping.getHandler(request);
      return handler != null;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Gửi response 404 với format ErrorResponse
   */
  private void sendNotFoundResponse(HttpServletResponse response,
      HttpServletRequest request) throws IOException {
    response.setStatus(HttpStatus.NOT_FOUND.value());
    response.setContentType("application/json;charset=UTF-8");

    String path = request.getRequestURI();
    String method = request.getMethod();

    ErrorResponse<Object> errorResponse = ErrorResponse.builder()
        .timestamp(new Date())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(String.format("No endpoint %s %s.", method, path))
        .path(path)
        .build();

    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}