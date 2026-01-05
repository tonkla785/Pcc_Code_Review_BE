package pccth.code.review.Backend.Service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueService {


    public String listIssues(){
        return "List issues";
    }
    public String getIssuesDetail(){
        return "Get issue details";
    }
    public String assignDeveloper(){
        return "Assign developer";
    }
    public String addComment(){
        return "Add comment";
    }
    public String upDateStatus(){
        return "Update status";
    }

}
