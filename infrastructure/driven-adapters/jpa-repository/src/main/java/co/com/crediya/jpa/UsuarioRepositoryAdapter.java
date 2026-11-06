package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UsuarioEntity;
import co.com.crediya.jpa.repository.UsuarioJpaRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryAdapter implements UserRepository {
    private final UsuarioJpaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByCorreoElectronico(email);
    }

    @Override
    @Transactional
    public User save(User user) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setIdUsuario(user.getId());
        entity.setNombre(user.getNombre());
        entity.setApellido(user.getApellido());
        entity.setFechaNacimiento(user.getFechaNacimiento());
        entity.setDireccion(user.getDireccion());
        entity.setTelefono(user.getTelefono());
        entity.setCorreoElectronico(user.getEmail());
        entity.setSalarioBase(user.getSalarioBase());

        UsuarioEntity saved;
        try {
            saved = repository.save(entity);
        } catch (DataIntegrityViolationException exception) {
            if (isEmailConstraintViolation(exception)) {
                throw new UserAlreadyExistsException();
            }
            throw exception;
        }

        return User.builder()
                .id(saved.getIdUsuario())
                .nombre(saved.getNombre())
                .apellido(saved.getApellido())
                .fechaNacimiento(saved.getFechaNacimiento())
                .direccion(saved.getDireccion())
                .telefono(saved.getTelefono())
                .email(saved.getCorreoElectronico())
                .salarioBase(saved.getSalarioBase())
                .build();
    }

    private boolean isEmailConstraintViolation(Throwable exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof ConstraintViolationException violation
                    && "uk_usuario_correo_electronico".equalsIgnoreCase(violation.getConstraintName())) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
