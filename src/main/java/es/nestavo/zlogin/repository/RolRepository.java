package es.nestavo.zlogin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.nestavo.zlogin.modelo.*;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    
	Optional<Rol> findByNombre(String nombre);
}
