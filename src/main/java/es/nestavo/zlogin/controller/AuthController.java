package es.nestavo.zlogin.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.*;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.nestavo.zlogin.execption.ResourceNotFoundException;
import es.nestavo.zlogin.modelo.PasswordResetToken;
import es.nestavo.zlogin.modelo.Usuario;
import es.nestavo.zlogin.repository.PasswordResetTokenRepository;
import es.nestavo.zlogin.repository.UsuarioRepository;
import es.nestavo.zlogin.servicio.UsuarioService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrecta.");
        }
        return "login";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@ModelAttribute @Valid Usuario usuario,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        // Verifica si el nombre de usuario ya existe
        if (usuarioService.existeUsuarioPorNombre(usuario.getUsername())) {
            // Agrega un mensaje de error que será mostrado en la página
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe. Por favor, elige otro.");
            return "redirect:/registro"; // Redirige a la página de creación
        }

        // Si hay errores de validación, vuelve a mostrar el formulario
        if (bindingResult.hasErrors()) {
            return "registro";
        }
        // Verifica si el EMAIL de usuario ya existe
        if (usuarioService.existeUsuarioPorEmail(usuario.getEmail())) {
            // Agrega un mensaje de error que será mostrado en la página
            redirectAttributes.addFlashAttribute("error", "El Email de usuario ya existe. Por favor, elige otro.");
            return "redirect:/registro"; // Redirige a la página de creación
        }

        // Si hay errores de validación, vuelve a mostrar el formulario
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        usuarioService.registrarUsuario(usuario); // Lógica para registrar el usuario
        redirectAttributes.addFlashAttribute("success", "Usuario registrado exitosamente. Inicie sesión");
        return "redirect:login"; // Redirigir a la página de login después de registrarse
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user";
    }

    //------------------------------------------------

    // Mostrar el formulario para solicitar la recuperación
    @GetMapping("/recuperarcontra")
    public String mostrarFormularioRecuperacion() {
        return "recuperaContra";
    }

    // Procesar el envío del correo de recuperación
    @PostMapping("/recuperarcontra")
    public String procesarRecuperacion(@RequestParam String email, Model model) {
        // Generar el token
        String token = usuarioService.generarTokenDeRecuperacion(email);
        
        // Enviar el correo con el token de recuperación
        usuarioService.enviarEmailRec(email, token);

        model.addAttribute("message", "Se ha enviado un enlace de recuperación a tu correo.");
        return "mensajeRec";  // Mostrar un mensaje de confirmación
    }

    // Muestra formulario cambiar contraseña
    @GetMapping("/reset/reset-password")
    public String mostrarFormularioRestablecimiento(@RequestParam String token, Model model) {
        log.info("Token recibido: {}", token);
        
        // Buscar el token en la base de datos
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
            .orElse(null);  // Cambia `null` por una excepción si lo prefieres

        // Si el token no se encuentra o ha expirado
        if (resetToken == null) {
            model.addAttribute("error", "Token no válido o expirado");
            log.error("Error: Token no encontrado o inválido");
            return "error";
        }
        
        // Verificar si el token ha expirado
        if (resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "El token ha expirado");
            log.error("Error: Token expirado");
            return "error";
        }

        // Si el token es válido, se pasa al modelo para usarlo en la vista
        model.addAttribute("token", token);
        return "restablecer-password";
    }

    // Procesar el restablecimiento de la contraseña
    @PostMapping("/reset/reset-password")
    @Transactional
    public String procesarRestablecimiento(@RequestParam String token, @RequestParam String nuevaPassword, Model model) {
        log.info("Token recibido en POST: {}", token);

        // Buscar el token en la base de datos
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Token no válido o no encontrado"));

        // Verificar si el token ha expirado
        if (resetToken.getFechaExpiracion() == null || resetToken.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El token ha expirado");
        }

        // Restablecer la contraseña
        usuarioService.restablecerContraseña(token, nuevaPassword);

        // Eliminar el token después de haber sido usado
        passwordResetTokenRepository.deleteBytoken(token);
        log.info("Token eliminado de la base de datos tras ser utilizado");

        model.addAttribute("message", "Tu contraseña ha sido restablecida con éxito.");
        return "mensaje2";
    }

}
