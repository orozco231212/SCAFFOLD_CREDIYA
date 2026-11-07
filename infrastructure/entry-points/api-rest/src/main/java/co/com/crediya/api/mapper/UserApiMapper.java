package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.UsuarioRequestDto;
import co.com.crediya.api.dto.UsuarioResponseDto;
import co.com.crediya.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserApiMapper {

    public User toDomain(UsuarioRequestDto request) {
        return User.builder()
                .nombre(request.nombre())
                .apellido(request.apellido())
                .fechaNacimiento(request.fechaNacimiento())
                .direccion(request.direccion())
                .telefono(request.telefono())
                .email(request.correoElectronico())
                .salarioBase(request.salarioBase())
                .build();
    }

    public UsuarioResponseDto toResponse(User user) {
        return new UsuarioResponseDto(
                user.getId(),
                user.getNombre(),
                user.getApellido(),
                user.getFechaNacimiento(),
                user.getDireccion(),
                user.getTelefono(),
                user.getEmail(),
                user.getSalarioBase()
        );
    }
}