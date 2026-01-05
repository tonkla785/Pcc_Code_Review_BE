package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {

    public String listIssues() {
        return "List issues";
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
