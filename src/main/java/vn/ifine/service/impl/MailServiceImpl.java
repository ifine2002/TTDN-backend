package vn.ifine.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import vn.ifine.service.MailService;

@Service
@Slf4j(topic = "MAIL-SERVICE-IMPL")
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender javaMailSender;
  private final SpringTemplateEngine templateEngine;

  @Value("${spring.mail.from}")
  private String mailFrom;

  @Override
  public void sendEmailSync(String recipients, String subject, String content, MultipartFile[] files, boolean isHtml) {
    // Prepare message using a Spring helper
    log.info("Sending....");
    MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
    try {
      MimeMessageHelper message = new MimeMessageHelper(mimeMessage,
          true, StandardCharsets.UTF_8.name());
      message.setFrom(mailFrom, "Ifine");
      if (recipients.contains(",")) {
        message.setTo(InternetAddress.parse(recipients));
      } else {
        message.setTo(recipients);
      }
      if (files != null) {
        for (MultipartFile file : files) {
          message.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
        }
      }
      message.setSubject(subject);
      message.setText(content, isHtml);
      this.javaMailSender.send(mimeMessage);
    } catch (MailException | MessagingException | UnsupportedEncodingException e) {
      System.out.println("ERROR SEND EMAIL: " + e);
    }
    log.info("Mail has been send successfully, recipients={}", recipients);
  }

  @Override
  @Async
  public void sendEmailFromTemplateSync(String recipients, String subject, String templateName, String fullName,
      String token, MultipartFile[] files) {
    Context context = new Context();
    String link = "http://localhost:8080/auth/verify?token=" + token;
    context.setVariable("fullName", fullName);
    context.setVariable("link", link);

    String content = this.templateEngine.process(templateName, context);
    this.sendEmailSync(recipients, subject, content, null, true);
  }
}
