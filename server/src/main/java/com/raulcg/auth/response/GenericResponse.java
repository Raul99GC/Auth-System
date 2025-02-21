package com.raulcg.auth.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GenericResponse<T> {

    private T data;
    private String message;
    private boolean status;
}
