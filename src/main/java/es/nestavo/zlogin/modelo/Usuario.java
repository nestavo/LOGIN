package es.nestavo.zlogin.modelo;



import java.util.HashSet;

import java.util.Set;

import jakarta.validation.constraints.*;


import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El nombre de usuario es obligatorio.")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres.")
    private String username;

    @Column(nullable = false)
    @NotBlank(message = "No puede estar vacio")
    @Size(min = 6,  message = "Minimo 6 caracteres la contraseña")
    private String password;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "No puede estar vacio")
    @Email(message = "Introduce un E-mail valido")
    private String email;
    
    
    @Column(name ="Nombre de la Empresa" ,nullable = false)
    @NotBlank(message = "No puede estar vacio")
    private String nombreEmpresa;
    
    @Column(nullable = false)
    @NotBlank(message = "No puede estar vacio")
    private String direccion;
    
    
    private String Cif;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<>();

    
}
