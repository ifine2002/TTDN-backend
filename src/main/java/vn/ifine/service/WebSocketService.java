package vn.ifine.service;

import org.springframework.stereotype.Service;

@Service
public interface WebSocketService {
  public void sendAdminBookNotification(String action, Object data);
  public void sendReviewNotification(String action, Long bookId, Object data);
}
