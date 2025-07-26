package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.ifine.service.UserService;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;



}
