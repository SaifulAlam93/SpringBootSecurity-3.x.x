package com.abc.SpringBootSecqurityEx.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {
    @NotNull
    private Boolean enabled;
}
