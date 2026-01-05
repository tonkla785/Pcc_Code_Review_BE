package pccth.code.review.Backend.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pccth.code.review.Backend.Service.IssueService;

@RestController
@RequestMapping("api")
public class IssueController {
    @Autowired
    private IssueService issueService;


    @GetMapping("/issues")
    public String getListIssues(){
        return issueService.listIssues();
    }

    @GetMapping("/issues/{id}")
    public String getIssuesDetail(){
        return issueService.getIssuesDetail();
    }
    @PutMapping("/issues/{id}/assign")
    public String putAssignDeveloper(){
        return issueService.assignDeveloper();
    }
    @PostMapping("/issues/{id}/comments")
    public String postAddComment(){
        return issueService.addComment();
    }
    @PutMapping("/issues/{id}/status")
    public String putUpDateStatus(){
        return issueService.upDateStatus();
    }




}
