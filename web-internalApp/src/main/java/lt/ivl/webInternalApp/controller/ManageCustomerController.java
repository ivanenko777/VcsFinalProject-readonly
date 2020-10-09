package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Customer;
import lt.ivl.webInternalApp.service.InternalCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/manage-customer")
public class ManageCustomerController {
    @Autowired
    private InternalCustomerService internalCustomerService;

    @GetMapping("/list")
    private String list(Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);
        return "manage-customer/list";
    }
}
