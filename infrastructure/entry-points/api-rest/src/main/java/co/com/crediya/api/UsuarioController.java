package co.com.crediya.api;

import co.com.crediya.api.dto.UsuarioRequestDto;
import co.com.crediya.api.dto.UsuarioResponseDto;
import co.com.crediya.api.mapper.UserApiMapper;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/usuarios", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UsuarioController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioController.class);

    private final UserUseCase userUseCase;
    private final UserApiMapper userApiMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDto> registrar(@Valid @RequestBody UsuarioRequestDto request) {
        LOGGER.info("Iniciando registro de usuario");

        // Convertimos el Request DTO a Modelo de Dominio usando el Mapper
        User user = userApiMapper.toDomain(request);

        // Ejecutamos el caso de uso
        User registeredUser = userUseCase.registerUser(user);

        // Convertimos el Modelo de Dominio a Response DTO usando el Mapper
        UsuarioResponseDto response = userApiMapper.toResponse(registeredUser);

        LOGGER.info("Usuario registrado correctamente, id={}", registeredUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UsuarioResponseDto> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDto request) {
        LOGGER.info("Iniciando actualización de usuario con id={}", id);

        User user = userApiMapper.toDomain(request);
        user.setId(id);

        User updatedUser = userUseCase.updateUser(user);
        UsuarioResponseDto response = userApiMapper.toResponse(updatedUser);

        LOGGER.info("Usuario actualizado correctamente, id={}", updatedUser.getId());
        return ResponseEntity.ok(response);
    }
}