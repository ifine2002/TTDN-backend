package vn.ifine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

// disable security
//@SpringBootApplication(exclude = {
//    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
//})
@EnableAsync
@SpringBootApplication
public class BackendServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendServiceApplication.class, args);
  }

}
