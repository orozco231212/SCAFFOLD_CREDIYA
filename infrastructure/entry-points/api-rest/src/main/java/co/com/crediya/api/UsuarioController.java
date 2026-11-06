package co.com.crediya.api;

import co.com.crediya.api.dto.UsuarioRequestDto;
import co.com.crediya.api.dto.UsuarioResponseDto;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsuarioController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    private final UserUseCase userUseCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDto> registrar(@Valid @RequestBody UsuarioRequestDto request) {
        LOGGER.info("Iniciando registro de usuario");

        User user = User.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .email(request.correoElectronico())
                .salarioBase(request.salarioBase())
                .build();

        User registeredUser = userUseCase.registerUser(user);
        UsuarioResponseDto response = new UsuarioResponseDto(
                registeredUser.getId(),
                registeredUser.getNombre(),
                registeredUser.getApellido(),
                registeredUser.getFechaNacimiento(),
                registeredUser.getDireccion(),
                registeredUser.getTelefono(),
                registeredUser.getEmail(),
                registeredUser.getSalarioBase()
        );

        LOGGER.info("Usuario registrado correctamente, id={}", registeredUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
