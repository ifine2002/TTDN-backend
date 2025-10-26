package vn.ifine.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.ifine.repository.PermissionRepository;
import vn.ifine.service.UserService;

@Configuration
@RequiredArgsConstructor
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

  private final UserService userService;


  @Bean
  PermissionInterceptor getPermissionInterceptor() {
    return new PermissionInterceptor(userService);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    String[] whiteList = {
        "/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/ws/**", "/*.html", "/swagger-ui.html", "/book/home-page",
        "/book/detail-book/**", "/book/explore", "/book/search", "/user/search", "/user/profile/**", "/book/list-book-user",
        "/favorite-book/books-of-user/**", "/category/list-upload"
    };
    registry.addInterceptor(getPermissionInterceptor())
        .excludePathPatterns(whiteList);
  }
}
