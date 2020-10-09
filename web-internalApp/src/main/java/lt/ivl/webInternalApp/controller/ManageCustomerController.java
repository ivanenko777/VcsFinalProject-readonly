package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.exception.CustomerExistsInDatabaseException;
import lt.ivl.webInternalApp.dto.CustomerDto;
import lt.ivl.webInternalApp.service.InternalCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @GetMapping("/{customer}/view")
    private String view(@PathVariable Customer customer, Model model) {
        model.addAttribute("customer", customer);
        return "manage-customer/view";
    }

    @GetMapping("/add")
    public String showCreateForm(Model model) {
        model.addAttribute("customerDto", new CustomerDto());
        return "manage-customer/add";
    }

    @PostMapping("/add")
    public String create(
            @Valid @ModelAttribute("customerDto") CustomerDto customerDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "manage-customer/add";
        }

        try {
            Customer customer = internalCustomerService.createCustomer(customerDto);
            return "redirect:/manage-customer/" + customer.getId() + "/view";
        } catch (CustomerExistsInDatabaseException e) {
            model.addAttribute("messageError", e.getMessage());
            return "manage-customer/add";
        }
    }
}
