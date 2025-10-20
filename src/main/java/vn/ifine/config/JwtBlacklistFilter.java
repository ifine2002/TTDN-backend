package vn.ifine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.ifine.exception.ErrorResponse;
import vn.ifine.service.RedisService;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistFilter extends OncePerRequestFilter {

  private final RedisService redisService;
  private final ObjectMapper objectMapper;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {

    try {
      String token = extractTokenFromRequest(request);

      if (token != null && redisService.isBlacklisted(token)) {

        log.warn("Blacklisted token detected");

        ErrorResponse<Object> errorResponse = ErrorResponse.<Object>builder()
            .timestamp(new Date())
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("Unauthorized")
            .message("Token has been revoked")
            .path(request.getRequestURI())
            .build();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        return;
      }

      filterChain.doFilter(request, response);

    } catch (Exception e) {
      log.error("Error in blacklist filter: {}", e.getMessage());
      filterChain.doFilter(request, response);
    }
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}