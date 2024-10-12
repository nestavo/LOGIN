package es.nestavo.zlogin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import es.nestavo.zlogin.modelo.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsername(String username);
	Optional<Usuario> findByUsernameOrEmail(String username, String email);
	Optional<Usuario> findByEmail(String email);
}
