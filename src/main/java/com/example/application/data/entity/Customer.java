package com.example.application.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.validation.constraints.Email;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Customer extends AbstractEntity {

    private String fullName;
    @Email
    private String email;
    private String phone;
    private Gender gender;
    private String occupation;
    private boolean important;


}
