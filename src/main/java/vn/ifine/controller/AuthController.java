package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.dto.request.ReqLoginDTO;
import vn.ifine.dto.request.ReqRegisterDTO;
import vn.ifine.dto.request.ReqResetPassword;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.dto.response.ResLoginDTO;
import vn.ifine.dto.response.ResUserAccount;
import vn.ifine.exception.InvalidTokenException;
import vn.ifine.model.User;
import vn.ifine.service.AuthService;
import vn.ifine.service.JwtService;
import vn.ifine.service.UserService;
import vn.ifine.service.impl.JwtServiceImpl;

@RestController
@RequestMapping("/auth")
@Slf4j(topic = "AUTH-CONTROLLER")
@Validated
@Tag(name = "Auth Controller")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final JwtService jwtService;

  @Value("${jwt.refresh-token-validity-in-seconds}")
  private long refreshTokenExpiration;

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<ResLoginDTO>> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
    log.info("Request login email={}", loginDTO.getEmail());
    // create access_token
    ResLoginDTO res = authService.login(loginDTO);

    // create refresh token
    String refresh_token = this.jwtService.createRefreshToken(loginDTO.getEmail(), res);

    // update user
    this.userService.updateUserToken(refresh_token, loginDTO.getEmail());

    // set refresh token vào cookie
    ResponseCookie resCookies = ResponseCookie
        .from("refresh_token", refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookies.toString())
        .body(ApiResponse.success("Login successfully", res));
  }

  @GetMapping("/refresh")
  public ResponseEntity<ApiResponse<ResLoginDTO>> getRefreshToken(
      @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) {
    log.info("refresh_token={}", refresh_token);
    // kiểm tra có truyền cookie không
    if (refresh_token.equals("abc")) {
      throw new InvalidTokenException("You do not have a refresh token in your cookie.");
    }

    // create new access_token
    ResLoginDTO res = authService.getRefreshToken(refresh_token);

    // create new refresh_token
    String new_refresh_token = this.jwtService.createRefreshToken(res.getUser().getEmail(), res);

    // update user
    this.userService.updateUserToken(new_refresh_token, res.getUser().getEmail());

    // set new refresh token vào cookie
    ResponseCookie resCookies = ResponseCookie
        .from("refresh_token", new_refresh_token)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpiration)
        .build();

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, resCookies.toString())
        .body(ApiResponse.success("Get refresh token successfully", res));

  }

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody ReqRegisterDTO req) {
    authService.register(req);
    return ResponseEntity.ok(ApiResponse.created("Already registered, please check your email to verify.", null));
  }

  @GetMapping("/resend-token")
  public ResponseEntity<ApiResponse<Void>> resendToken(@RequestParam String email) {
    User user = userService.getUserByEmail(email);
    authService.createAndSendToken(user);
    return ResponseEntity.ok(ApiResponse.success("Send token reset password successful!", null));
  }

  @GetMapping("/send-reset-token")
  public ResponseEntity<ApiResponse<Void>> sendResetToken(@RequestParam String email) {
    authService.sendTokenResetPassword(email);
    return ResponseEntity.ok(ApiResponse.success("Resend verify token successful!", null));
  }

  @GetMapping("/verify")
  public ResponseEntity<ApiResponse<Void>> verify(@RequestParam String email, @RequestParam String token) {
    authService.verifyToken(email, token);
    return ResponseEntity.ok(ApiResponse.success("Account Verification Successful!", null));
  }

  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestParam String token, @RequestBody ReqResetPassword request) {
    authService.resetPassword(token, request);
    return ResponseEntity.ok(ApiResponse.success("Reset password successful!", null));
  }

  @GetMapping("/account")
  public ResponseEntity<ApiResponse<ResUserAccount>> getAccount() {
    ResUserAccount userAccount = authService.getAccount();
    return ResponseEntity.ok()
        .body(ApiResponse.success("Fetch account successfully", userAccount));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<Void>> logout() {
    String email = JwtServiceImpl.getCurrentUserLogin().isPresent() ? JwtServiceImpl.getCurrentUserLogin().get() : "";
    if (email.equals("")) {
      throw new InvalidTokenException("Access Token invalid");
    }

    // update refresh token = null
    this.userService.updateUserToken(null, email);

    // remove refresh token cookie
    ResponseCookie deleteSpringCookie = ResponseCookie
        .from("refresh_token", null)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(0)
        .build();
    // Clear SecurityContext
    SecurityContextHolder.clearContext();
//
//    // Create a new empty authentication
    SecurityContextHolder.getContext().setAuthentication(null);
//

    return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
        .body(ApiResponse.success("Logout User", null));
  }
}
