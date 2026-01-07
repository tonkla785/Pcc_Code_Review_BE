package pccth.code.review.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Entity.IssueEntity;
import pccth.code.review.Backend.Service.IssueService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping("issues")
    public List<IssueEntity> getListIssues() {
        return issueService.listIssues();
    }

    @GetMapping("issues/{id}")
    public String getIssueDetail(@PathVariable Long id) {
        return issueService.getIssueDetail(id);
    }

    @PutMapping("issues/{id}/assign")
    public String assignDeveloper(@PathVariable Long id) {
        return issueService.assignDeveloper(id);
    }

    @PostMapping("issues/{id}/comments")
    public String addComment(@PathVariable Long id) {
        return issueService.addComment(id);
    }

    @PutMapping("issues/{id}/status")
    public String updateStatus(@PathVariable Long id) {
        return issueService.updateStatus(id);
    }
}

