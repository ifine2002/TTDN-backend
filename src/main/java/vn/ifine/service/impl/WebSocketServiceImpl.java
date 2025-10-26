package vn.ifine.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import vn.ifine.service.WebSocketService;

@Slf4j(topic = "WEBSOCKET-SERVICE-IMPL")
@RequiredArgsConstructor
@Service
public class WebSocketServiceImpl implements WebSocketService {

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void sendAdminBookNotification(String action, Object data) {
    Map<String, Object> notification = createNotificationPayload(action, data);
    messagingTemplate.convertAndSend("/topic/admin-books", notification);
  }

  @Override
  public void sendReviewNotification(String action, Long bookId, Object data) {
    Map<String, Object> notification = createNotificationPayload(action, data);
    messagingTemplate.convertAndSend("/topic/reviews/" + bookId, notification);
  }
  private Map<String, Object> createNotificationPayload(String action, Object data) {
    Map<String, Object> notification = new HashMap<>();
    notification.put("action", action);
    notification.put("data", data);
    notification.put("timestamp", LocalDateTime.now());
    return notification;
  }
}
