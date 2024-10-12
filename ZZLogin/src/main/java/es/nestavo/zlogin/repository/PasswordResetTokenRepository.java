package es.nestavo.zlogin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import es.nestavo.zlogin.modelo.PasswordResetToken;

public interface PasswordResetTokenRepository  extends JpaRepository<PasswordResetToken, Long>{
	
	Optional<PasswordResetToken> findByToken (String token);
	
    void deleteBytoken(String token);
}
