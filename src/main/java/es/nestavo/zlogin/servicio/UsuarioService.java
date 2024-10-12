package es.nestavo.zlogin.servicio;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.nestavo.zlogin.controller.AuthController;
import es.nestavo.zlogin.modelo.PasswordResetToken;
import es.nestavo.zlogin.modelo.Rol;
import es.nestavo.zlogin.modelo.Usuario;
import es.nestavo.zlogin.repository.PasswordResetTokenRepository;
import es.nestavo.zlogin.repository.RolRepository;
import es.nestavo.zlogin.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class UsuarioService implements UserDetailsService {
	private static final Logger log = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private RolRepository rolRepository;

	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Override
	public UserDetails loadUserByUsername(String usernameoremail) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUsernameOrEmail(usernameoremail, usernameoremail)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado" + usernameoremail));

		Set<SimpleGrantedAuthority> roles = usuario.getRoles().stream()
				.map(rol -> new SimpleGrantedAuthority(rol.getNombre())).collect(Collectors.toSet());
		return new User(usuario.getUsername(), usuario.getPassword(), roles);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void registrarUsuario(Usuario usuario) {
		// Codifica la contraseña antes de guardar
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		// Asignar el rol "USER"
		Rol rolUser = rolRepository.findByNombre("ROLE_USER")
				.orElseThrow(() -> new RuntimeException("Rol no encontrado: ROLE_USER")); // Manejar si no se encuentra
																							// el rol

		// Asigna el rol al usuario
		usuario.setRoles(Set.of(rolUser)); // Asegúrate de asignar el rol aquí
		usuarioRepository.save(usuario);
	}

	public boolean existeUsuarioPorNombre(String username) {
		return usuarioRepository.findByUsername(username).isPresent();
	}

	public boolean existeUsuarioPorEmail(String email) {
		return usuarioRepository.findByEmail(email).isPresent();
	}

	@Autowired
	private JavaMailSender mailSender;

	public String enviarEmailRec(String email, String token) {

		// Verificar si el email está registrado en la base de datos
		boolean emailExiste = usuarioRepository.findByEmail(email).isPresent();

		if (!emailExiste) {

			return "error";

		}

		token = generarTokenDeRecuperacion(email);
		String url = "http://localhost:4647/reset/reset-password?token=" + token;

		// Crear un mensaje simple
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email); // Destinatario
		message.setSubject("Recuperación de contraseña"); // Asunto del correo
		message.setText("Para restablecer tu contraseña, haz clic en el siguiente enlace: " + url); // Cuerpo del
																									// mensaje

		// Enviar el correo
		mailSender.send(message);

		return "Correo enviado";
	}

	private Map<String, String> tokenEmailMap = new HashMap<>();

	public String generarTokenDeRecuperacion(String email) {
		String token = UUID.randomUUID().toString();
		PasswordResetToken resetToken = new PasswordResetToken();
		resetToken.setToken(token);
		resetToken.setEmail(email);
		resetToken.setFechaCreacion(LocalDateTime.now());
		resetToken.setFechaExpiracion(LocalDateTime.now().plusMinutes(5)); // Expira en 5 min

		PasswordResetToken savedToken = passwordResetTokenRepository.save(resetToken);
		log.info("Token guardado: " + savedToken.getToken());
		return token;
	}

	@Transactional
	public void restablecerContraseña(String token, String nuevaPassword) {
		PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
				.orElseThrow(() -> new RuntimeException("Token no válido o no encontrado"));

		if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
			throw new RuntimeException("El token ha expirado");
		}

		Usuario usuario = usuarioRepository.findByEmail(resetToken.getEmail()).orElseThrow(
				() -> new RuntimeException("Usuario no encontrado para el email: " + resetToken.getEmail()));

		usuario.setPassword(passwordEncoder.encode(nuevaPassword));
		usuarioRepository.save(usuario);

		// Eliminar el token una vez utilizado
		passwordResetTokenRepository.deleteBytoken(token);
	}

	public String obtenerEmailPorToken(String token) {
		return tokenEmailMap.get(token); // Obtener el email asociado con el token
	}

	public Map<String, String> getTokenEmailMap() {
		return tokenEmailMap;
	}

	public Usuario buscarPorEmail(String email) {
		return usuarioRepository.findByEmail(email).orElse(null); // Devuelve null si no encuentra al usuario
	}

}
