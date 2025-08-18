package vn.ifine.config;

import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.ifine.exception.CustomAuthenticationException;
import vn.ifine.service.UserService;

@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {

  private final UserService userService;

  public UserDetailsCustom(UserService userService) {
    this.userService = userService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    vn.ifine.model.User user = this.userService.getUserByEmail(username);
    if (user == null) {
      throw new UsernameNotFoundException("Invalid username/password");
    }

    switch (user.getStatus()) {
      case NONE:
        throw new CustomAuthenticationException("Account not verified. Please check your email to activate your account.", HttpStatus.UNAUTHORIZED);
      case INACTIVE:
        throw new CustomAuthenticationException("Your account has been deactivated. Please contact support.", HttpStatus.FORBIDDEN);
      case DELETED:
        throw new CustomAuthenticationException("Account deleted", HttpStatus.NOT_FOUND);
    }

    return new User(
        user.getEmail(),
        user.getPassword(),
        true, // enabled
        true, // accountNonExpired
        true, // credentialsNonExpired
        true, // accountNonLocked
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
  }
}
