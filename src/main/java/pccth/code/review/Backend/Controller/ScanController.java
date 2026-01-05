package pccth.code.review.Backend.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScanController {

    @GetMapping("/tao")
    public String get(){
        return "Bunlung";
    }
}
