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
            validateEmployee(emp);  // this validate Employee method checks where all the mandatory fields are mentioned
            employeeRepo.save(emp);
            return "Employee details added successfully";
        }
        catch (InvalidEmployeeDataException ex) {
            return ex.getMessage();
        }
    }

    private void validateEmployee(Employee employee){
        if(employee == null) {
            throw new InvalidEmployeeDataException("Invalid employee data ");
        }
    }

//    private boolean isValidEmployeeData(Employee employee){
//        if(employee.getEmployeeId() == null ||
//           employee.getFirstName() == null ||
//           employee.getLastName() == null ||
//           employee.getEmail() == null ||
//           employee.getDoj() == null ||
//           employee.getPhoneNumber() == null ||
//           employee.getSalary()<=0) {
//            throw new InvalidEmployeeDataException("Some of the employee data missing or invalid");
//        }
//        return true;
//    }

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
        double yearlySalary = calculateYearlySalary(employee);
        double taxAmount = calculateTaxAmount(yearlySalary);
        double cessAmount = calculateCessAmount(yearlySalary);
        return new EmployeeTaxInfo(employee.getEmployeeId(),
                                   employee.getFirstName(),
                                   employee.getLastName(),
                                   yearlySalary,
                                   taxAmount,
                                   cessAmount);
    }


    private double calculateYearlySalary(Employee employee) {
        LocalDate currentDate = LocalDate.now();
        LocalDate joiningDate =employee.getDoj();

        int monthsWorked = (int) (joiningDate.until(currentDate).toTotalMonths());

        boolean isJoinedInCurrentFinancialYear = joiningDate.getMonthValue() >= 4
                                      && joiningDate.getYear() == currentDate.getYear() - 1;
        int monthsToExclude = 0;
        if(isJoinedInCurrentFinancialYear){
            monthsToExclude = joiningDate.getMonthValue() - 4;
        }
        else {
            monthsToExclude = 12;
        }
        int numOfMonths = Math.max(0, monthsWorked - monthsToExclude);

        double yearlySalary = employee.getSalary() * numOfMonths;
        return yearlySalary;
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
