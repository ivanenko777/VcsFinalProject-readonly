package lt.ivl.webInternalApp;

import lt.ivl.components.domain.*;
import lt.ivl.components.exception.CustomerNotFoundInDBException;
import lt.ivl.components.exception.InvalidStatusException;
import lt.ivl.components.repository.CustomerRepository;
import lt.ivl.components.repository.EmployeeRepository;
import lt.ivl.components.repository.RepairRepository;
import lt.ivl.components.repository.RepairStatusHistoryRepository;
import lt.ivl.webInternalApp.dto.*;
import lt.ivl.webInternalApp.service.InternalCustomerService;
import lt.ivl.webInternalApp.service.InternalEmployeeService;
import lt.ivl.webInternalApp.service.InternalRepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InternalDataLoader implements ApplicationRunner {
    @Autowired
    private InternalCustomerService internalCustomerService;

    @Autowired
    private InternalEmployeeService internalEmployeeService;

    @Autowired
    private InternalRepairService internalRepairService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RepairRepository repairRepository;

    @Autowired
    private RepairStatusHistoryRepository repairStatusHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        // Customers
        Customer customer = new Customer();
        customer.setFirstName("Klientas");
        customer.setLastName("1 Int");
        customer.setEmail("k1@demo.lt");
        customer.setPhone("1234567890");
        try {
            internalCustomerService.createCustomer(new CustomerDto(customer));
        } catch (Exception e) {
            e.getMessage();
        }

        // Employees
        Employee employeeAdmin = new Employee();
        employeeAdmin.setFirstName("Admin");
        employeeAdmin.setLastName("Int");
        employeeAdmin.setEmail("admin@demo.lt");
        employeeAdmin.setPhone("1234567890");
        employeeAdmin.getRoles().add(EmployeeRole.ADMIN);

        Employee employeeManager = new Employee();
        employeeManager.setFirstName("Manager");
        employeeManager.setLastName("Int");
        employeeManager.setEmail("manager@demo.lt");
        employeeManager.setPhone("1234567890");
        employeeManager.getRoles().add(EmployeeRole.MANAGER);

        Employee employeeTechnician = new Employee();
        employeeTechnician.setFirstName("Technician");
        employeeTechnician.setLastName("Int");
        employeeTechnician.setEmail("technician@demo.lt");
        employeeTechnician.setPhone("1234567890");
        employeeTechnician.getRoles().add(EmployeeRole.TECHNICIAN);

        List<Employee> employees = Arrays.asList(employeeAdmin, employeeManager, employeeTechnician);
        for (Employee employee : employees) {
            try {
                employee = internalEmployeeService.createEmployeeAccount(new EmployeeDto(employee));
                EmployeeResetPasswordToken resetPasswordToken = internalEmployeeService.createPasswordResetTokenForEmployeeAccount(employee);
                internalEmployeeService.resetPasswordAndActivateEmployeeAccount(employee, new ResetPasswordDto("a", "a"), resetPasswordToken);
            } catch (Exception e) {
                e.getMessage();
            }
        }

        // Repairs
        repairStatusHistoryRepository.deleteAll();
        repairRepository.deleteAll();
        Customer rCustomer = customerRepository.findByEmail("k1@demo.lt").get();
        Employee rEmployeeAdmin = employeeRepository.findByEmail("admin@demo.lt").get();

        Repair repairWithWarranty = new Repair(rCustomer, "Fenas", "Bosch", "123", "SN123", true, "Neveikia");
        Repair repairWithoutWarranty = new Repair(rCustomer, "TV", "Samsung", "654", "SN654", false, "Neveikia");

        RepairDto repairDtoWithWarranty = new RepairDto(repairWithWarranty);
        RepairDto repairDtoWithoutWarranty = new RepairDto(repairWithoutWarranty);

        RepairStatusStoredDto repairStatusStoredDto = new RepairStatusStoredDto();
        RepairStatusNoteDto repairStatusNoteDto = new RepairStatusNoteDto();
        RepairStatusQuestionDto repairStatusQuestionDto = new RepairStatusQuestionDto();

        // STATUS PENDING
        try {
            internalRepairService.createRepair(repairDtoWithWarranty, rEmployeeAdmin);
            internalRepairService.createRepair(repairDtoWithoutWarranty, rEmployeeAdmin);
        } catch (InvalidStatusException | CustomerNotFoundInDBException e) {
            e.printStackTrace();
        }

        // STATUS CONFIRMED
        repairDtoWithWarranty.setConfirmRepair(true);
        repairDtoWithoutWarranty.setConfirmRepair(true);
        try {
            internalRepairService.createRepair(repairDtoWithWarranty, rEmployeeAdmin);
            internalRepairService.createRepair(repairDtoWithoutWarranty, rEmployeeAdmin);
        } catch (InvalidStatusException | CustomerNotFoundInDBException e) {
            e.printStackTrace();
        }

        // STATUS DIAGNOSTIC_WAITING
        try {
            repairStatusStoredDto.setStored("1/8");
            repairStatusNoteDto.setNote("");
            Repair repairWithWarranty02 = internalRepairService.createRepair(repairDtoWithWarranty, rEmployeeAdmin);
            internalRepairService.storeDevice(repairWithWarranty02, repairStatusStoredDto, repairStatusNoteDto, rEmployeeAdmin);
            Repair repairWithoutWarranty02 = internalRepairService.createRepair(repairDtoWithoutWarranty, rEmployeeAdmin);
            internalRepairService.storeDevice(repairWithoutWarranty02, repairStatusStoredDto, repairStatusNoteDto, rEmployeeAdmin);
        } catch (InvalidStatusException | CustomerNotFoundInDBException e) {
            e.printStackTrace();
        }

        // STATUS DIAGNOSTIC
        try {
            repairStatusStoredDto.setStored("2/6");
            repairStatusNoteDto.setNote("");
            Repair repairWithWarranty03 = internalRepairService.createRepair(repairDtoWithWarranty, rEmployeeAdmin);
            repairWithWarranty03 = internalRepairService.storeDevice(repairWithWarranty03, repairStatusStoredDto, repairStatusNoteDto, rEmployeeAdmin);
            repairWithWarranty03 = internalRepairService.startDiagnostic(repairWithWarranty03, rEmployeeAdmin);
            repairStatusNoteDto.setNote("Nemokamas remontas");
            repairWithWarranty03 = internalRepairService.finishDiagnostic(repairWithWarranty03, repairStatusStoredDto, repairStatusNoteDto, rEmployeeAdmin);
            repairWithWarranty03 = internalRepairService.startRepair(repairWithWarranty03, rEmployeeAdmin);
            repairStatusNoteDto.setNote("Remontas atliktas");
            repairStatusQuestionDto.setAnswer(true);
            repairWithWarranty03 = internalRepairService.finishRepair(repairWithWarranty03, repairStatusStoredDto, repairStatusNoteDto, repairStatusQuestionDto, rEmployeeAdmin);
            internalRepairService.completeRepair(repairWithWarranty03, repairStatusNoteDto, rEmployeeAdmin);


            repairStatusQuestionDto.setAnswer(null);
            repairStatusNoteDto.setNote("");
            Repair repairWithoutWarranty03 = internalRepairService.createRepair(repairDtoWithoutWarranty, rEmployeeAdmin);
            repairWithoutWarranty03 = internalRepairService.storeDevice(repairWithoutWarranty03, repairStatusStoredDto, repairStatusNoteDto, rEmployeeAdmin);
            repairWithoutWarranty03 = internalRepairService.startDiagnostic(repairWithoutWarranty03, rEmployeeAdmin);
            repairStatusNoteDto.setNote("Remonto kaina 20 eur");
            repairWithoutWarranty03 = internalRepairService.finishDiagnostic(repairWithoutWarranty03, repairStatusStoredDto, repairStatusNoteDto, rEmployeeAdmin);
            repairWithoutWarranty03 = internalRepairService.cancelPayment(repairWithoutWarranty03, rEmployeeAdmin);
            repairStatusNoteDto.setNote("");
            internalRepairService.completeRepair(repairWithoutWarranty03, repairStatusNoteDto, rEmployeeAdmin);
        } catch (InvalidStatusException | CustomerNotFoundInDBException e) {
            e.printStackTrace();
        }


        rCustomer.setActive(true);
        rCustomer.setPassword(passwordEncoder.encode("a"));
        customerRepository.save(rCustomer);
    }
}
