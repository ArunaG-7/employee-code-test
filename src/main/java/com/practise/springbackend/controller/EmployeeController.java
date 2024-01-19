package com.practise.springbackend.controller;

import com.practise.springbackend.entity.Employee;
import com.practise.springbackend.model.EmployeeTaxInfo;
import com.practise.springbackend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emp")
public class EmployeeController {

    @Autowired
    EmployeeService empService;

    // 1.RESTAPI for adding the employee
    @PostMapping("/add-employee")
    public ResponseEntity<String> addEmp(@RequestBody Employee emp){
        String result = empService.addEmployee (emp);
        return ResponseEntity.ok(result);

    }

    // 2. RESTAPI for calculating the employee tax deduction

    @GetMapping("/tax-deduction")
    public List<EmployeeTaxInfo> getEmployeeTaxDeduction() {
        return empService.calculateTaxDeductionForCurrentYear();
    }
}
