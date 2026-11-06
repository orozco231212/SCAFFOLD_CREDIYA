package co.com.crediya.model.user;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String telefono;
    private String email;
    private BigDecimal salarioBase;
}
