package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Repository.IssueRepository;

import java.util.List;

@Service
public class IssueService {
    private final IssueRepository issueRepository;

    public IssueService(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }

    public List<IssueEntity> listIssues() {
        return issueRepository.findAll();
    }

    public String getIssueDetail(Long id) {
        return "Get issue details: " + id;
    }

    public String assignDeveloper(Long id) {
        return "Assign developer to issue: " + id;
    }

    public String addComment(Long id) {
        return "Add comment to issue: " + id;
    }

    public String updateStatus(Long id) {
        return "Update status of issue: " + id;
    }
}
