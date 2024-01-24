package com.practise.springbackend.entity;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name="employee")
public class Employee {

    @Id
    @Column(name = "employee_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String employeeId;


    @Column(name = "first_name")
    @NotBlank
    private String firstName;

    @Column(name = "last_name")
    @NotBlank
    private String lastName;

    @Column(name = "email")
    @Email
    private String email;

    @ElementCollection
    @NotEmpty
    @Valid
    private List<@NotBlank @Pattern(regexp = "\\d{10}")String> phoneNumber;

    @Column(name = "doj")
    @NotNull
    private LocalDate doj;

    @Column(name = "salary")
    @NotNull
    @Positive
    private Double salary;

}
