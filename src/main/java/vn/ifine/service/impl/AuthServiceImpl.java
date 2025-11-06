package vn.ifine.service.impl;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;
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
import vn.ifine.dto.JwtInfo;
import vn.ifine.dto.request.ReqLoginDTO;
import vn.ifine.dto.request.ReqRegisterDTO;
import vn.ifine.dto.request.ReqResetPassword;
import vn.ifine.dto.response.ResLoginDTO;
import vn.ifine.dto.response.ResLoginDTO.UserInsideToken;
import vn.ifine.dto.response.ResLoginDTO.UserLogin;
import vn.ifine.dto.response.ResUserAccount;
import vn.ifine.exception.CustomAuthenticationException;
import vn.ifine.exception.CustomException;
import vn.ifine.exception.InvalidTokenException;
import vn.ifine.model.RedisToken;
import vn.ifine.model.Role;
import vn.ifine.model.User;
import vn.ifine.model.VerificationToken;
import vn.ifine.repository.RedisTokenRepository;
import vn.ifine.repository.RoleRepository;
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
  private final RoleRepository roleRepository;
  private final RedisTokenRepository redisTokenRepository;

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
          userDB.getImage(),
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
          currentUserDB.getImage(),
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
      user.setFullName(registerDTO.getFullName());
      user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
      userRepository.save(user);
      if (user.getStatus() != UserStatus.NONE) {
        throw new CustomAuthenticationException("This email has already been used",
            HttpStatus.BAD_REQUEST);
      }
      this.createAndSendToken(user);
      return;
    }

    if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())){
      throw new CustomException("Password and confirm password do not match");
    }

    Role roleUser = roleRepository.findByName("USER");

    User user = User.builder()
        .email(registerDTO.getEmail())
        .password(passwordEncoder.encode(registerDTO.getPassword()))
        .fullName(registerDTO.getFullName())
        .role(roleUser)
        .status(UserStatus.NONE)
        .build();
    userRepository.save(user);
    log.info("Save register user on database success, id={}", user.getId());
    // todo gửi mã
    this.createAndSendToken(user);
  }

  @Override
  public void createAndSendToken(User user) {
    tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

    String token = String.format("%06d", new Random().nextInt(1_000_000));
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setExpiryDate(LocalDateTime.now().plusMinutes(5));
    tokenRepository.save(verificationToken);
    log.info("Create verification_token success, idToken={}", verificationToken.getId());
    mailService.sendEmailFromTemplateSync(user.getEmail(), "Verify account", "verification",
        user.getFullName(), token, null);
  }

  @Override
  public ResUserAccount getAccount() {
    String email =
        JwtServiceImpl.getCurrentUserLogin().isPresent() ? JwtServiceImpl.getCurrentUserLogin()
            .get() : "";

    User user = userService.getUserByEmail(email);

    ResUserAccount userAccount = new ResUserAccount();
    if(user != null){
      UserLogin userDTO = new UserLogin(user.getId(), user.getEmail(), user.getFullName(), user.getImage(), user.getRole());
      userAccount.setUser(userDTO);
    }
    log.info("Get info account success email={}", userAccount.getUser().getEmail());
    return userAccount;
  }

  @Override
  public void sendTokenResetPassword(String email) {
    User user = userService.getUserByEmail(email);
    String resetToken = jwtService.createResetToken(email);
    mailService.sendResetTokenFromTemplateSync(user.getEmail(), "Reset password", "reset-password",
        user.getFullName(), resetToken, null);
  }

  @Override
  public void resetPassword(String token, ReqResetPassword request) {
    // 1. Kiểm tra resetToken token có hợp lệ
    Jwt decodedToken = jwtService.checkValidResetToken(token);
    String email = decodedToken.getSubject();
    User user = userService.getUserByEmail(email);

    if (!request.getNewPassword().equals(request.getConfirmPassword())) {
      throw new CustomException("New password and confirm password do not match");
    }

    String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
    user.setPassword(encodedNewPassword);

    userRepository.save(user);
  }

  @Override
  public void logout(String token) {
    JwtInfo jwtInfo = jwtService.parseToken(token);
    String jwtId = jwtInfo.getJwtId();
    Date expiredTime = jwtInfo.getExpiredTime();
    Date now = new Date();
    if(expiredTime.before(new Date())){
      return;
    }

    RedisToken redisToken = RedisToken.builder()
        .jwtId(jwtId)
        .expiredTime(expiredTime.getTime() - now.getTime())
        .build();

    redisTokenRepository.save(redisToken);
  }

  public void verifyToken(String email, String token) {
    User user = userService.getUserByEmail(email);

    VerificationToken vt = tokenRepository.findByTokenAndUser(token, user)
        .orElseThrow(
            () -> new CustomAuthenticationException("Token invalid", HttpStatus.BAD_REQUEST));

    if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
      throw new CustomAuthenticationException("Token has expired", HttpStatus.BAD_REQUEST);
    }

    User userDB = vt.getUser();
    userDB.setStatus(UserStatus.ACTIVE);
    userRepository.save(userDB);
    tokenRepository.delete(vt);
  }
}
