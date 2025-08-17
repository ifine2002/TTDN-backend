package vn.ifine.configuration;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import vn.ifine.exception.InvalidTokenException;
import vn.ifine.util.TokenType;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

  @Value("${jwt.access-token}")
  private String jwtKeyAccess;

  @Value("${jwt.refresh-token}")
  private String jwtKeyRefresh;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {
    String[] whiteList = { "/", "/auth/**", "/storage/**", "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html" };
    http
        .csrf(c -> c.disable())
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(
            authorize -> authorize
                .requestMatchers(whiteList).permitAll()
                .anyRequest().authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(Customizer.withDefaults())
            .authenticationEntryPoint(customAuthenticationEntryPoint))
            // Cấu hình bộ lọc JWT chỉ áp dụng cho các đường dẫn không nằm trong whitelist
        .formLogin(f -> f.disable())
        // cấu hình sử dụng mô hình stateless
        .sessionManagement((sessionManagement) -> sessionManagement
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    return http.build();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    grantedAuthoritiesConverter.setAuthorityPrefix("");
    grantedAuthoritiesConverter.setAuthoritiesClaimName("permission");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  // cấu hình decoder giải mã access_token
  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(
        geKey(TokenType.ACCESS)) // Lấy SecretKey để giải mã
        .macAlgorithm(MacAlgorithm.HS512).build(); // Thuật toán mã hóa (HS512)
    return token -> {
      try {
        return jwtDecoder.decode(token);
      } catch (Exception e) {
        System.out.println(">>> JWT error: " + e.getMessage());
        throw e;
      }
    };
  }

  @Bean("accessTokenEncoder")
  public JwtEncoder accessTokenEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(geKey(TokenType.ACCESS)));
  }

  @Bean("refreshTokenEncoder")
  public JwtEncoder refreshTokenEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(geKey(TokenType.REFRESH)));
  }

  private SecretKey geKey(TokenType type) {
    switch (type) {
      case ACCESS -> {
        byte[] keyBytes = Base64.from(jwtKeyAccess).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, MacAlgorithm.HS512.getName());
      }
      case REFRESH -> {
        byte[] keyBytes = Base64.from(jwtKeyRefresh).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, MacAlgorithm.HS512.getName());
      }
      default -> throw new InvalidTokenException("Invalid token type");
    }
  }
}
