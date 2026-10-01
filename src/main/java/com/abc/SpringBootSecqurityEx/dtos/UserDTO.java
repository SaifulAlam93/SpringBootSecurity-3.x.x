package com.abc.SpringBootSecqurityEx.dtos;

import com.abc.SpringBootSecqurityEx.enums.ERole;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Set;

@Getter
@Setter
public class UserDTO {
    private String userName;
    private String userFirstName;
    private String userLastName;
    private String email;
    private Boolean enabled;
    private Set<ERole> roles;
    private OffsetDateTime dateCreated;
}
