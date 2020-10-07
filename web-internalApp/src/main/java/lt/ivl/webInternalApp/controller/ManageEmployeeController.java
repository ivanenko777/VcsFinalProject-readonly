package lt.ivl.webInternalApp.controller;

import lt.ivl.components.domain.Employee;
import lt.ivl.webInternalApp.service.InternalEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
