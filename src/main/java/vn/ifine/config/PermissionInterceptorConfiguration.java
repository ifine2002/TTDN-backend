package vn.ifine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.ifine.service.UserService;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

  private final UserService userService;

  public PermissionInterceptorConfiguration(UserService userService) {
    this.userService = userService;
  }

  @Bean
  PermissionInterceptor getPermissionInterceptor() {
    return new PermissionInterceptor(userService);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    String[] whiteList = {
        "/auth/**", "/v3/api-docs/**",
        "/swagger-ui/**", "/ws/**", "/*.html",
        "/swagger-ui.html", "/book/home-page", "/book/detail-book/**", "/book/explore",
        "/book/search", "/user/search", "/user/profile/**", "/follow/list-following",
        "/book/list-book-user", "/favorite-book/books-of-user/**"
    };
    registry.addInterceptor(getPermissionInterceptor())
        .excludePathPatterns(whiteList);
  }
}
