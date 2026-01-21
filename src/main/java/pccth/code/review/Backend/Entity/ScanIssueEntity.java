package pccth.code.review.Backend.Entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "scan_issues")
public class ScanIssueEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_id", nullable = false)
    private ScanEntity scan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private IssueEntity issue;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public ScanEntity getScan() {
        return scan;
    }

    public void setScan(ScanEntity scan) {
        this.scan = scan;
    }

    public IssueEntity getIssue() {
        return issue;
    }

    public void setIssue(IssueEntity issue) {
        this.issue = issue;
    }
}

