package co.com.crediya.usecase.user;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.UserAlreadyExistsException;
import co.com.crediya.model.user.exceptions.UserValidationException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UserUseCase {
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserRepository userRepository;

    public User registerUser(User user) {
        validate(user);

        String normalizedEmail = user.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new UserAlreadyExistsException();
        }

        return userRepository.save(user.toBuilder().email(normalizedEmail).build());
    }

    private void validate(User user) {
        if (user == null
                || isBlank(user.getNombre())
                || isBlank(user.getApellido())
                || isBlank(user.getEmail())
                || user.getSalarioBase() == null) {
            throw new UserValidationException("Nombre, apellido, correo electrónico y salario base son obligatorios.");
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail().trim()).matches()) {
            throw new UserValidationException("El correo electrónico no tiene un formato válido.");
        }

        if (user.getSalarioBase().compareTo(BigDecimal.ZERO) < 0
                || user.getSalarioBase().compareTo(MAX_SALARY) > 0) {
            throw new UserValidationException("El salario base debe estar entre 0 y 15000000.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
