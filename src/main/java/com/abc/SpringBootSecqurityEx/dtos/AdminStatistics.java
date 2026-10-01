package com.abc.SpringBootSecqurityEx.dtos;

import com.abc.SpringBootSecqurityEx.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatistics {
    private long totalUsers;
    private long enabledUsers;
    private List<ERole> roles;
}
