package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserAlreadyExistsException;
import co.com.crediya.model.user.exceptions.UserValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository);
    }

    @Test
    void registersUserAndNormalizesEmail() {
        User user = validUser();
        when(userRepository.existsByEmail("persona@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.<User>getArgument(0).toBuilder().id(1L).build());

        User result = userUseCase.registerUser(user);

        assertEquals(1L, result.getId());
        assertEquals("persona@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void rejectsAlreadyRegisteredEmail() {
        when(userRepository.existsByEmail("persona@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userUseCase.registerUser(validUser()));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void rejectsInvalidEmail() {
        User user = validUser().toBuilder().email("correo-invalido").build();

        assertThrows(UserValidationException.class, () -> userUseCase.registerUser(user));

        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void rejectsSalaryOutsideAllowedRange() {
        User user = validUser().toBuilder().salarioBase(new BigDecimal("15000000.01")).build();

        assertThrows(UserValidationException.class, () -> userUseCase.registerUser(user));

        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void acceptsSalaryAtBothRangeBoundaries() {
        when(userRepository.existsByEmail("persona@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User minimumSalaryUser = validUser().toBuilder().salarioBase(BigDecimal.ZERO).build();
        User maximumSalaryUser = validUser().toBuilder().salarioBase(new BigDecimal("15000000")).build();

        assertEquals(BigDecimal.ZERO, userUseCase.registerUser(minimumSalaryUser).getSalarioBase());
        assertEquals(new BigDecimal("15000000"), userUseCase.registerUser(maximumSalaryUser).getSalarioBase());

        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void rejectsNegativeSalary() {
        User user = validUser().toBuilder().salarioBase(new BigDecimal("-0.01")).build();

        assertThrows(UserValidationException.class, () -> userUseCase.registerUser(user));

        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    void rejectsMissingRequiredFields() {
        User user = validUser().toBuilder().nombre(" ").build();

        assertThrows(UserValidationException.class, () -> userUseCase.registerUser(user));

        verify(userRepository, never()).existsByEmail(any());
    }

    private User validUser() {
        return User.builder()
                .nombre("Ana")
                .apellido("Pérez")
                .email(" Persona@Example.com ")
                .salarioBase(new BigDecimal("2500000"))
                .build();
    }
}
