package com.raulcg.auth.requires;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequire {

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*\\W)(?!.*(\\d)\\1\\1).*$",
            message = "La contraseña debe contener al menos una mayúscula, un número y un carácter especial, y no debe tener secuencias de 3 o más números.")
    private String password;

    @NotBlank
    private String token;
}
