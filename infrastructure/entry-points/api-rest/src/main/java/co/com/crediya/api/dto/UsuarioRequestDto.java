package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UsuarioRequestDto(
        @NotBlank String nombre,
        @NotBlank String apellido,
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        @JsonProperty("correo_electronico") @NotBlank @Email String correoElectronico,
        @JsonProperty("salario_base") @NotNull @DecimalMin("0.00") @DecimalMax("15000000.00") BigDecimal salarioBase
) {
}
