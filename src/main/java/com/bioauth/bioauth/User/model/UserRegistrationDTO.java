package com.bioauth.bioauth.User.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter @Setter @ToString
public class UserRegistrationDTO {
    @NotBlank
    private String admissionNumber;

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    private String password;
    private String confirmPassword;

    @AssertTrue
    private Boolean terms;
}
