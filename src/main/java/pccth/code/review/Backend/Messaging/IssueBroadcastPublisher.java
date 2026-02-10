package pccth.code.review.Backend.Messaging;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IssueBroadcastPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public IssueBroadcastPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastIssueChange(IssueChangeEvent event) {
        messagingTemplate.convertAndSend("/topic/issues", event);
        System.out.println("BROADCAST ISSUE: " + event.getAction() + " - " + event.getIssueId());
    }

    public static class IssueChangeEvent {
        private String action; // "UPDATED"
        private UUID issueId;

        public IssueChangeEvent() {
        }

        public IssueChangeEvent(String action, UUID issueId) {
            this.action = action;
            this.issueId = issueId;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public UUID getIssueId() {
            return issueId;
        }

        public void setIssueId(UUID issueId) {
            this.issueId = issueId;
        }
    }
}
