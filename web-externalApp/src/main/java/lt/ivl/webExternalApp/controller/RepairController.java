package lt.ivl.webExternalApp.controller;

import lt.ivl.webExternalApp.dto.CustomerDto;
import lt.ivl.webExternalApp.dto.RepairDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/repair")
public class RepairController {
    @GetMapping("/index")
    public String index(){
        return "repair/index";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model){
        model.addAttribute("repair", new RepairDto());
        return "repair/add";
    }
}
