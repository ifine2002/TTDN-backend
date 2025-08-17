package vn.ifine.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import vn.ifine.exception.ErrorResponse;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
  // Sử dụng BearerTokenAuthenticationEntryPoint mặc định của Spring
  private final AuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();

  // ObjectMapper để chuyển đổi object thành JSON
  private final ObjectMapper mapper;

  // Constructor injection
  public CustomAuthenticationEntryPoint(ObjectMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    // Constructor injection
    this.delegate.commence(request, response, authException);

    // 2. Set response type là JSON
    response.setContentType("application/json;charset=UTF-8");

    // Lấy message lỗi chi tiết
    String errorMessage = Optional.ofNullable((authException.getCause()))
        .map(Throwable::getMessage)
        .orElse(authException.getMessage());

    // 3. Tạo custom response
    ErrorResponse<Object> errorResponse = ErrorResponse.<Object>builder()
        .timestamp(new Date())
        .status(HttpStatus.UNAUTHORIZED.value())
        .error(errorMessage)
        .message("Invalid token (expired, incorrect format, or no JWT header)...")
        .path(request.getRequestURI())
        .build();
    // 6. Chuyển response thành JSON và gửi về client
    mapper.writeValue(response.getWriter(), errorResponse);
  }
}
