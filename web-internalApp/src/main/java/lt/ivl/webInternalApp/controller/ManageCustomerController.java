package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Customer;
import lt.ivl.components.exception.CustomerExistsInDatabaseException;
import lt.ivl.webInternalApp.dto.CustomerDto;
import lt.ivl.webInternalApp.service.InternalCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/manage-customer")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
public class ManageCustomerController {
    @Autowired
    private InternalCustomerService internalCustomerService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Customer> customerList = internalCustomerService.findAll();
        model.addAttribute("customerList", customerList);
        return "manage-customer/list";
    }

    @GetMapping("/{customer}/view")
    public String view(@PathVariable Customer customer, Model model) {
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
            int customerId = customer.getId();
            return "redirect:/manage-customer/" + customerId + "/view";
        } catch (CustomerExistsInDatabaseException e) {
            model.addAttribute("messageError", "Klientas su tokiu el. pašto adresu jau užregistruota.");
            return "manage-customer/add";
        }
    }

    @GetMapping("{customer}/edit")
    public String showUpdateForm(@PathVariable("customer") Customer customer, Model model) {
        CustomerDto customerDto = new CustomerDto(customer);
        int customerId = customer.getId();

        model.addAttribute("customerDto", customerDto);
        model.addAttribute("customerId", customerId);
        return "manage-customer/edit";
    }

    @PostMapping("{customer}/edit")
    public String update(
            @PathVariable("customer") Customer customer,
            @Valid @ModelAttribute("customerDto") CustomerDto customerDto,
            BindingResult bindingResult,
            Model model
    ) {
        int customerId = customer.getId();
        model.addAttribute("customerDto", customerDto);
        model.addAttribute("customerId", customerId);

        if (bindingResult.hasErrors()) {
            return "manage-customer/edit";
        }

        try {
            internalCustomerService.updateCustomer(customer, customerDto);
            return "redirect:/manage-customer/{customer}/view";
        } catch (CustomerExistsInDatabaseException e) {
            model.addAttribute("messageError", "Klientas su tokiu el. pašto adresu jau užregistruota.");
            return "manage-customer/add";
        }
    }
}
