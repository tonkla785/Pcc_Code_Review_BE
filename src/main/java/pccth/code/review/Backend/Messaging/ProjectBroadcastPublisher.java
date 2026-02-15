package pccth.code.review.Backend.Messaging;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
public class ProjectBroadcastPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ProjectBroadcastPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcastProjectChange(ProjectChangeEvent event) {
        messagingTemplate.convertAndSend("/topic/projects", event);
    }

    public static class ProjectChangeEvent {
        private String action; // "ADDED", "UPDATED", "DELETED"
        private UUID projectId;
        private String projectName;

        public ProjectChangeEvent() {
        }

        public ProjectChangeEvent(String action, UUID projectId, String projectName) {
            this.action = action;
            this.projectId = projectId;
            this.projectName = projectName;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public UUID getProjectId() {
            return projectId;
        }

        public void setProjectId(UUID projectId) {
            this.projectId = projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }
    }
}
