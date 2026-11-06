package co.com.crediya.jpa.repository;

import co.com.crediya.jpa.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Long> {
    boolean existsByCorreoElectronico(String correoElectronico);
}
