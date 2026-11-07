package co.com.crediya.jpa;

import co.com.crediya.jpa.entity.UsuarioEntity;
import co.com.crediya.jpa.repository.UsuarioJpaRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserAlreadyExistsException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioRepositoryAdapterTest {
    @Mock
    private UsuarioJpaRepository repository;

    private UsuarioRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UsuarioRepositoryAdapter(repository);
    }

    @Test
    void checksWhetherEmailAlreadyExists() {
        when(repository.existsByCorreoElectronico("ana@example.com")).thenReturn(true);

        boolean exists = adapter.existsByEmail("ana@example.com");

        assertTrue(exists);
        verify(repository).existsByCorreoElectronico("ana@example.com");
    }

    @Test
    void savesUserAndMapsAllFields() {
        User user = validUser();
        when(repository.save(any(UsuarioEntity.class))).thenAnswer(invocation -> {
            UsuarioEntity entity = invocation.getArgument(0);
            entity.setIdUsuario(42L);
            return entity;
        });

        User savedUser = adapter.save(user);

        ArgumentCaptor<UsuarioEntity> entityCaptor = ArgumentCaptor.forClass(UsuarioEntity.class);
        verify(repository).save(entityCaptor.capture());
        UsuarioEntity savedEntity = entityCaptor.getValue();
        assertEquals(user.getNombre(), savedEntity.getNombre());
        assertEquals(user.getApellido(), savedEntity.getApellido());
        assertEquals(user.getFechaNacimiento(), savedEntity.getFechaNacimiento());
        assertEquals(user.getDireccion(), savedEntity.getDireccion());
        assertEquals(user.getTelefono(), savedEntity.getTelefono());
        assertEquals(user.getEmail(), savedEntity.getCorreoElectronico());
        assertEquals(user.getSalarioBase(), savedEntity.getSalarioBase());

        assertEquals(42L, savedUser.getId());
        assertEquals(user.getNombre(), savedUser.getNombre());
        assertEquals(user.getApellido(), savedUser.getApellido());
        assertEquals(user.getFechaNacimiento(), savedUser.getFechaNacimiento());
        assertEquals(user.getDireccion(), savedUser.getDireccion());
        assertEquals(user.getTelefono(), savedUser.getTelefono());
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(user.getSalarioBase(), savedUser.getSalarioBase());
    }

    @Test
    void mapsEmailUniqueConstraintViolationToBusinessException() {
        ConstraintViolationException constraintViolation = new ConstraintViolationException(
                "duplicate email",
                null,
                "uk_usuario_correo_electronico"
        );
        when(repository.save(any(UsuarioEntity.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate email", constraintViolation));

        assertThrows(UserAlreadyExistsException.class, () -> adapter.save(validUser()));
    }

    @Test
    void propagatesUnrelatedDataIntegrityViolations() {
        ConstraintViolationException constraintViolation = new ConstraintViolationException(
                "other constraint",
                null,
                "uk_usuario_otra_columna"
        );
        DataIntegrityViolationException persistenceException =
                new DataIntegrityViolationException("other constraint", constraintViolation);
        when(repository.save(any(UsuarioEntity.class))).thenThrow(persistenceException);

        DataIntegrityViolationException thrown = assertThrows(
                DataIntegrityViolationException.class,
                () -> adapter.save(validUser())
        );

        assertEquals(persistenceException, thrown);
    }

    private User validUser() {
        return User.builder()
                .nombre("Ana")
                .apellido("Pérez")
                .fechaNacimiento(LocalDate.of(1998, 6, 15))
                .direccion("Calle 1")
                .telefono("3001234567")
                .email("ana@example.com")
                .salarioBase(new BigDecimal("2500000"))
                .build();
    }
}
