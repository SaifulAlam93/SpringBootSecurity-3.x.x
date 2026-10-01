package com.abc.SpringBootSecqurityEx.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;
}
