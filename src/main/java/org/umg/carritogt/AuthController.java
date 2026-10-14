package org.umg.carritogt;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository repository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthController(UsuarioRepository repository, PasswordEncoder encoder, JwtService jwtService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = repository.findByCorreo(request.email())
                .filter(Usuario::getActivo)
                .filter(u -> encoder.matches(request.password(), u.getContrasenaHash()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Correo o contraseña incorrectos"));
        usuario.setUltimoLogin(LocalDateTime.now());
        repository.save(usuario);
        return new LoginResponse(jwtService.generar(usuario), UsuarioResumen.de(usuario));
    }

    public record LoginRequest(@NotBlank String email, @NotBlank String password) {
    }

    public record UsuarioResumen(Long id, String nombre, String email, String rol) {

        static UsuarioResumen de(Usuario usuario) {
            return new UsuarioResumen(usuario.getId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
        }
    }

    public record LoginResponse(String token, UsuarioResumen user) {
    }
}