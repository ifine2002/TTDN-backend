package vn.ifine.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.ifine.exception.PermissionException;
import vn.ifine.model.Permission;
import vn.ifine.model.Role;
import vn.ifine.model.User;
import vn.ifine.repository.PermissionRepository;
import vn.ifine.service.PermissionService;
import vn.ifine.service.UserService;
import vn.ifine.service.impl.JwtServiceImpl;

@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

  private final UserService userService;

  @Override
  @Transactional
  public boolean preHandle(
      HttpServletRequest request,
      HttpServletResponse response, Object handler)
      throws Exception {

    String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
    String requestURI = request.getRequestURI();
    String httpMethod = request.getMethod();
    System.out.println(">>> RUN preHandle");
    System.out.println(">>> path= " + path);
    System.out.println(">>> httpMethod= " + httpMethod);
    System.out.println(">>> requestURI= " + requestURI);

    // check permission
    String email = JwtServiceImpl.getCurrentUserLogin().isPresent() == true
        ? JwtServiceImpl.getCurrentUserLogin().get()
        : "";
    if (email != null && !email.isEmpty()) {
      User user = this.userService.getUserByEmail(email);
      Role role = user.getRole();
      if (role != null) {
        List<Permission> permissions = role.getPermissions();
        for (Permission permission: permissions){
          System.out.println(permission.getApiPath());
        }

        boolean isAllow = permissions.stream().anyMatch(item -> item.getApiPath().equals(path)
            && item.getMethod().equals(httpMethod)
        );

        if (isAllow == false) {
          throw new PermissionException("You do not have permission to access this endpoint.");
        }
      } else {
        throw new PermissionException("You do not have permission to access this endpoint.");
      }
    }
    return true;
  }
}