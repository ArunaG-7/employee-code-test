package com.practise.springbackend.service;

import com.practise.springbackend.entity.Employee;
import com.practise.springbackend.exception.InvalidEmployeeDataException;
import com.practise.springbackend.model.EmployeeTaxInfo;
import com.practise.springbackend.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    public String addEmployee(Employee emp){

        try {
            validateEmployee(emp);  // this validateEmployee method checks where all the mandatory fields are mentioned
            employeeRepo.save(emp);
            return "Employee details added successfully";
        }
        catch (InvalidEmployeeDataException ex) {
            return ex.getMessage();
        }
    }

    private void validateEmployee(Employee employee){
        if(employee == null || !isValidEmployeeData(employee)) {
            throw new InvalidEmployeeDataException("Invalid employee data ");
        }
    }

    private boolean isValidEmployeeData(Employee employee){
        if(employee.getEmployeeId() == null ||
           employee.getFirstName() == null ||
           employee.getLastName() == null ||
           employee.getEmail() == null ||
           employee.getDoj() == null ||
           employee.getPhoneNumber() == null ||
           employee.getSalary()<=0) {
            throw new InvalidEmployeeDataException("Some of the employee data missing or invalid");
        }
        return true;
    }

    public List<EmployeeTaxInfo> calculateTaxDeductionForCurrentYear() {
        List<Employee> employees = employeeRepo.findAll();
        List<EmployeeTaxInfo> employeeTaxInfoList = new ArrayList<>();

        for(Employee emp : employees) {
            EmployeeTaxInfo taxInfo = calculateTaxInfo(emp);
            employeeTaxInfoList.add(taxInfo);
        }
        return employeeTaxInfoList;
    }

    private EmployeeTaxInfo calculateTaxInfo(Employee employee){
        double totalSalary = calculateTotalSalary(employee);
        double taxAmount = calculateTaxAmount(totalSalary);
        double cessAmount = calculateCessAmount(totalSalary);
        return new EmployeeTaxInfo(employee.getEmployeeId(),
                                   employee.getFirstName(),
                                   employee.getLastName(),
                                   totalSalary,
                                   taxAmount,
                                   cessAmount);
    }


    private double calculateTotalSalary(Employee employee) {
        LocalDate date = LocalDate.now();
        int numOfMonths = 12 - employee.getDoj().getMonthValue() + (employee.getDoj().getYear() == date.getYear() ? 1 :0);
        double lossOfPayPerDay = employee.getSalary() / 30;
        double totalSalary = employee.getSalary() * numOfMonths;
        return totalSalary;
    }

    private double calculateTaxAmount(double totalSalary){
        double taxAmount = Math.max(0, totalSalary-250000);
        if(totalSalary <= 250000){
            return 0;
        } else if (totalSalary <= 500000) {
            return 0.05 * taxAmount;
        }
        else if (totalSalary <= 1000000) {
            return 0.1 * (taxAmount-250000) + 12500;
        }
        else{
            return 0.2 * (taxAmount-1000000) + 12500 + 50000;
        }
    }

    // method to collect additional 2% cess for the amount more than 2500000
    private double calculateCessAmount(double totalSalary){
        double cessAmount = Math.max(0, totalSalary-2500000);
        return 0.02 * cessAmount;
    }

}
