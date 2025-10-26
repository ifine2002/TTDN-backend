package vn.ifine.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface MailService {

  void sendEmailSync(String recipients, String subject, String content, MultipartFile[] files,
      boolean isHtml);

  void sendEmailFromTemplateSync(String recipients, String subject, String templateName, String fullName, String token,
      MultipartFile[] files);

  void sendResetTokenFromTemplateSync(String recipients, String subject, String templateName, String fullName, String token,
      MultipartFile[] files);
}
