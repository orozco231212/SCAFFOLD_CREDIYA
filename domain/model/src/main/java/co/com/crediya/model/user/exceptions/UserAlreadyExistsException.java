package co.com.crediya.model.user.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("El correo electrónico ya está registrado.");
    }
}
