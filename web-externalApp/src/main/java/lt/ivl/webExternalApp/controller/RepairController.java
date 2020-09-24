package lt.ivl.webExternalApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repair")
public class RepairController {
    @GetMapping("/index")
    public String index(){
        return "repair/index";
    }
}
