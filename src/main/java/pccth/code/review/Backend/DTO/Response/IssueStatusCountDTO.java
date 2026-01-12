package pccth.code.review.Backend.DTO.Response;

public class IssueStatusCountDTO {
    private String status;
    private Long count;

    public IssueStatusCountDTO(String status, Long count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
