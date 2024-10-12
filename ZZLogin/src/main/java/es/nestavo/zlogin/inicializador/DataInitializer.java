//package es.nestavo.zlogin.inicializador;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import es.nestavo.zlogin.modelo.*;
//
//import es.nestavo.zlogin.repository.RolRepository;
//import es.nestavo.zlogin.repository.*;
//
//@Component
//public class DataInitializer implements CommandLineRunner{
//
//	@Autowired
//	private UsuarioRepository usuarioRepository;
//	
//	@Autowired
//	private RolRepository rolRepository;
//	
//	
//	@Override
//	public void run(String... args) throws Exception {
//		  Rol adminRol = new Rol();
//	        adminRol.setNombre("ROLE_ADMIN");
//	        rolRepository.save(adminRol);
//
//	        Rol userRol = new Rol();
//	        userRol.setNombre("ROLE_USER");
//	        rolRepository.save(userRol);
//
//	        Usuario admin = new Usuario();
//	        admin.setUsername("admin");
//	        admin.setPassword(new BCryptPasswordEncoder().encode("admin"));
//	        admin.getRoles().add(adminRol);
//	        usuarioRepository.save(admin);
//
//	        Usuario user = new Usuario();
//	        user.setUsername("user");
//	        user.setPassword(new BCryptPasswordEncoder().encode("user"));
//	        user.getRoles().add(userRol);
//	        usuarioRepository.save(user);
//		
//	}
//
//}
