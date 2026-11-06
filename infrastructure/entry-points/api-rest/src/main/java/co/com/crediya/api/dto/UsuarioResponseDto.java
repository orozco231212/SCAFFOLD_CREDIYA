package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UsuarioResponseDto(
        @JsonProperty("id_usuario") Long idUsuario,
        String nombre,
        String apellido,
        @JsonProperty("fecha_nacimiento") LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        @JsonProperty("correo_electronico") String correoElectronico,
        @JsonProperty("salario_base") BigDecimal salarioBase
) {
}
