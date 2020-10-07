package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Employee;
import lt.ivl.components.domain.EmployeeRole;
import lt.ivl.webInternalApp.dto.EmployeeDto;
import lt.ivl.webInternalApp.service.InternalEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/manage-employee")
public class ManageEmployeeController {
    @Autowired
    private InternalEmployeeService internalEmployeeService;

    @GetMapping("/list")
    public String list(Model model) {
        List<Employee> employeeList = internalEmployeeService.findAll();
        model.addAttribute("employeeList", employeeList);
        return "manage-employee/list";
    }

    @GetMapping("/{employee}/view")
    public String view(@PathVariable("employee") Employee employee, Model model) {
        model.addAttribute("employee", employee);
        return "manage-employee/view";
    }

    @GetMapping("{employee}/edit")
    public String showUpdateForm(@PathVariable("employee") Employee employee, Model model) {
        EmployeeDto employeeDto = new EmployeeDto(employee);
        int employeeId = employee.getId();
        List<EmployeeRole> roleList = Arrays.asList(EmployeeRole.values());

        model.addAttribute("employeeId", employeeId);
        model.addAttribute("employeeDto", employeeDto);
        model.addAttribute("roles", roleList);
        return "manage-employee/edit";
    }

    @PostMapping("{employee}/edit")
    public String update(
            @PathVariable("employee") Employee employee,
            @Valid @ModelAttribute("employeeDto") EmployeeDto employeeDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            List<EmployeeRole> roleList = Arrays.asList(EmployeeRole.values());
            int employeeId = employee.getId();

            model.addAttribute("roles", roleList);
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("messageError", "Formoje yra klaidų");
            return "manage-employee/edit";
        }
        internalEmployeeService.update(employee, employeeDto);
        return "redirect:/manage-employee/{employee}/view";
    }
}
