package pccth.code.review.Backend.Messaging;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Publisher for broadcasting project changes (add/edit/delete) to all users
 */
@Component
public class ProjectBroadcastPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public ProjectBroadcastPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Broadcast project change to all connected users
     * 
     * @param event ProjectChangeEvent containing action and projectId
     */
    public void broadcastProjectChange(ProjectChangeEvent event) {
        messagingTemplate.convertAndSend("/topic/projects", event);
        System.out.println("BROADCAST PROJECT: " + event.getAction() + " - " + event.getProjectId());
    }

    /**
     * Event class for project changes
     */
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
