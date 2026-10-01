package com.abc.SpringBootSecqurityEx.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    private String password;
}
