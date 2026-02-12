package pccth.code.review.Backend.DTO.Response;

import java.util.UUID;

public class UserWsEvent {
    private UUID userId;
    private String status;

    public UserWsEvent() {}

    public UserWsEvent(UUID userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}