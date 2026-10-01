package com.abc.SpringBootSecqurityEx.util;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ErrorResponse <T>{


    private int status_code;
    private String status;
    private String reason;
    private List<T> data;


}

