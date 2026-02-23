package pccth.code.review.Backend.Messaging;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IssueBroadcastPublisher {

    private final IssueStatusPublisher issueStatusPublisher;

    public IssueBroadcastPublisher(IssueStatusPublisher issueStatusPublisher) {
        this.issueStatusPublisher = issueStatusPublisher;
    }

    public void broadcastIssueChange(IssueChangeEvent event) {
        issueStatusPublisher.publish(event);
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
