package pccth.code.review.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Service.IssueService;

@RestController
@RequestMapping("/issues")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping
    public String getListIssues() {
        return issueService.listIssues();
    }

    @GetMapping("/{id}")
    public String getIssueDetail(@PathVariable Long id) {
        return issueService.getIssueDetail(id);
    }

    @PutMapping("/{id}/assign")
    public String assignDeveloper(@PathVariable Long id) {
        return issueService.assignDeveloper(id);
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id) {
        return issueService.addComment(id);
    }

    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id) {
        return issueService.updateStatus(id);
    }
}

