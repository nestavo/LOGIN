package es.nestavo.zlogin.configuration;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Obtener los roles del usuario autenticado
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isUser = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"));

        // Redirigir dependiendo del rol
        if (isAdmin) {
            response.sendRedirect("/admin");  // Página para admins
        } else if (isUser) {
            response.sendRedirect("/user");        // Página para usuarios
        } else {
            response.sendRedirect("/login?error");      // Si no tiene roles válidos, redirigir al login con error
        }
    }
}

