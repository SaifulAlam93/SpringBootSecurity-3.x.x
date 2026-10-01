package com.abc.SpringBootSecqurityEx.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {
    @Size(max = 255)
    private String firstName;

    @Size(max = 255)
    private String lastName;

    @Email
    @Size(max = 255)
    private String email;
}
