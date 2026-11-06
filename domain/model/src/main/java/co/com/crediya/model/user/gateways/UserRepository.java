package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;

public interface UserRepository {
    boolean existsByEmail(String email);

    User save(User user);
}
