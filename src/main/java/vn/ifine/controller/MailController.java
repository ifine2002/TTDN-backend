package vn.ifine.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import vn.ifine.dto.response.ApiResponse;
import vn.ifine.service.MailService;

@RestController
@RequestMapping("/mail")
@Slf4j(topic = "MAIL-CONTROLLER")
@RequiredArgsConstructor
@Validated
@Tag(name = "Mail Controller")
public class MailController {

  private final MailService mailService;

  @PostMapping("/send-email")
  public ResponseEntity<ApiResponse<Void>> sendSimpleEmail(
      @RequestParam String recipients,
      @RequestParam String subject,
      @RequestParam String templateName,
      @RequestParam String fullName,
      @RequestParam String link,
      @RequestParam(required = false) MultipartFile[] files) {
    this.mailService.sendEmailFromTemplateSync(recipients, subject, templateName, fullName, link, files);
    return ResponseEntity.ok().body(ApiResponse.success("Send email successfully", null));
  }

}
