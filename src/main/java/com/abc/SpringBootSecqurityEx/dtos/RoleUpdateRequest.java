package com.abc.SpringBootSecqurityEx.dtos;

import com.abc.SpringBootSecqurityEx.enums.ERole;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RoleUpdateRequest {
    @NotEmpty
    private Set<ERole> roles;
}
