package vn.ifine.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import vn.ifine.dto.request.ReqLoginDTO;
import vn.ifine.dto.request.ReqRegisterDTO;
import vn.ifine.dto.response.ResLoginDTO;
import vn.ifine.dto.response.ResLoginDTO.UserInsideToken;
import vn.ifine.dto.response.ResLoginDTO.UserLogin;
import vn.ifine.dto.response.ResUserAccount;
import vn.ifine.exception.CustomAuthenticationException;
import vn.ifine.exception.InvalidTokenException;
import vn.ifine.model.User;
import vn.ifine.model.VerificationToken;
import vn.ifine.repository.UserRepository;
import vn.ifine.repository.VerificationTokenRepository;
import vn.ifine.service.AuthService;
import vn.ifine.service.JwtService;
import vn.ifine.service.MailService;
import vn.ifine.service.UserService;
import vn.ifine.util.UserStatus;

@Service
@Slf4j(topic = "AUTH-SERVICE-IMPL")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserService userService;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final VerificationTokenRepository tokenRepository;
  private final MailService mailService;

  @Override
  public ResLoginDTO login(ReqLoginDTO loginDTO) {
    log.info("Request login email={}", loginDTO.getEmail());
    // Nạp input gồm username/password vào Security
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginDTO.getEmail(), loginDTO.getPassword());
    // xác thực người dùng => cần viết hàm loadUserByUsername
    Authentication authentication = authenticationManagerBuilder.getObject()
        .authenticate(authenticationToken);
    // Set thông tin người dùng đăng nhập vào context (có thể sử dụng sau này)
    SecurityContextHolder.getContext().setAuthentication(authentication);

    ResLoginDTO res = new ResLoginDTO();

    ResLoginDTO.UserInsideToken userToken = new UserInsideToken();

    User userDB = this.userService.getUserByEmail(loginDTO.getEmail());

    if (userDB != null) {
      userToken.setId(userDB.getId());
      userToken.setEmail(userDB.getEmail());
      userToken.setFullName(userDB.getFullName());
      ResLoginDTO.UserLogin userLogin = new UserLogin(
          userDB.getId(),
          userDB.getEmail(),
          userDB.getFullName(),
          userDB.getRole());
      res.setUser(userLogin);
    }

    // create a token
    String access_token = this.jwtService.createAccessToken(authentication.getName(), res);
    res.setAccessToken(access_token);

    return res;
  }

  @Override
  public ResLoginDTO getRefreshToken(String refresh_token) {
    // 1. Kiểm tra refresh token có hợp lệ
    Jwt decodedToken = jwtService.checkValidRefreshToken(refresh_token);
    String email = decodedToken.getSubject();

    // check user by token + email có khớp với db không
    User currentUser = this.userService.getUserByRefreshAndEmail(refresh_token, email);
    if (currentUser == null) {
      throw new InvalidTokenException("Refresh Token is invalid");
    }
    // tạo access token mới
    ResLoginDTO res = new ResLoginDTO();
    User currentUserDB = this.userService.getUserByEmail(email);
    if (currentUserDB != null) {
      ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
          currentUserDB.getId(),
          currentUserDB.getEmail(),
          currentUserDB.getFullName(),
          currentUserDB.getRole());
      res.setUser(userLogin);
    }
    // create a new access token
    String access_token = this.jwtService.createAccessToken(email, res);
    res.setAccessToken(access_token);
    return res;
  }

  @Override
  public void register(ReqRegisterDTO registerDTO) {
    log.info("Request register user, email={}", registerDTO.getEmail());
    boolean existUser = userService.isEmailExist(registerDTO.getEmail());
    if (existUser) {
      User user = userService.getUserByEmail(registerDTO.getEmail());
      if (user.getStatus() != UserStatus.NONE) {
        throw new CustomAuthenticationException("This email has already been used",
            HttpStatus.BAD_REQUEST);
      }
      createAndSendToken(user);
      return;
    }

    User user = User.builder()
        .email(registerDTO.getEmail())
        .password(passwordEncoder.encode(registerDTO.getPassword()))
        .fullName(registerDTO.getFullName())
        .status(UserStatus.NONE)
        .build();
    userRepository.save(user);
    log.info("Save register user on database success, id={}", user.getId());
    // todo gửi mã
    createAndSendToken(user);
  }

  private void createAndSendToken(User user) {
    tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
    tokenRepository.save(verificationToken);
    log.info("Create verification_token success, idToken={}", verificationToken.getId());
    mailService.sendEmailFromTemplateSync(user.getEmail(), "Verify account", "verification",
        user.getFullName(), token, null);
  }

  public void verifyToken(String token) {
    VerificationToken vt = tokenRepository.findByToken(token)
        .orElseThrow(
            () -> new CustomAuthenticationException("Token invalid", HttpStatus.BAD_REQUEST));

    if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new CustomAuthenticationException("Token has expired", HttpStatus.BAD_REQUEST);
    }

    User user = vt.getUser();
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);
    tokenRepository.delete(vt);
  }

  @Override
  public ResUserAccount getAccount() {
    String email =
        JwtServiceImpl.getCurrentUserLogin().isPresent() ? JwtServiceImpl.getCurrentUserLogin()
            .get() : "";

    User user = userService.getUserByEmail(email);
    ResUserAccount userAccount = new ResUserAccount();
    if(user != null){
      userAccount.setId(user.getId());
      userAccount.setEmail(user.getEmail());
      userAccount.setFullName(user.getFullName());
      userAccount.setRole(user.getRole());
    }
    log.info("Get info account success email={}", userAccount.getEmail());
    return userAccount;
  }
}
