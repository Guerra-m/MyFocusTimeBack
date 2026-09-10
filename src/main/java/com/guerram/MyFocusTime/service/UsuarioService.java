package com.guerram.MyFocusTime.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.guerram.MyFocusTime.config.JwtUtil;
import com.guerram.MyFocusTime.dto.UsuarioCreateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.exception.ConflictoException;
import com.guerram.MyFocusTime.exception.CredencialesInvalidasException;
import com.guerram.MyFocusTime.exception.RecursoNoEncontradoException;
import com.guerram.MyFocusTime.exception.SolicitudInvalidaException;
import com.guerram.MyFocusTime.mapper.Mapper;
import com.guerram.MyFocusTime.model.Rol;
import com.guerram.MyFocusTime.model.Usuario;
import com.guerram.MyFocusTime.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService implements IUsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private GoogleIdTokenVerifier googleIdTokenVerifier;

    @Override
    public UsuarioDTO crearUsuario(UsuarioCreateDTO dto) {
        Usuario usuario = Usuario.builder()
                .name(dto.getName())
                .mail(dto.getMail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .rol(Rol.USER)
                .build();
        return Mapper.toDto(repo.save(usuario));
    }

    @Override
    public UsuarioDTO traerUsuario(Long id) {
        return repo.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(()-> new RecursoNoEncontradoException("Usuario no encontrado"));
    }
    public Usuario traerUsuarioEntity(Long id){
        return repo.findById(id)
                .orElseThrow(()-> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    // Mismo mensaje para "no existe el mail" y "contraseña incorrecta":
    // distinguirlos permite averiguar qué correos están registrados.
    private static final String CREDENCIALES_INVALIDAS = "Correo o contraseña incorrectos";

    public UsuarioDTO login (String mail, String password) {
        Usuario usuario = repo.findByMail(mail)
                .orElseThrow(() -> new CredencialesInvalidasException(CREDENCIALES_INVALIDAS));

        if(!passwordEncoder.matches(password, usuario.getPassword())){
            throw new CredencialesInvalidasException(CREDENCIALES_INVALIDAS);
        }
        String token = jwtUtil.generateToken(usuario.getId(), usuario.getMail());

        UsuarioDTO dto = Mapper.toDto(usuario);
        dto.setToken(token);
        return dto;
    }

    // Login federado con Google Identity Services (ID token verificado del
    // lado del backend, sin authorization-code flow ni client secret).
    // Sigue AUTN-10 de la biblioteca de seguridad: GoogleIdTokenVerifier ya
    // valida firma, iss, aud (restringido al client-id propio) y exp; acá
    // agregamos el chequeo de email_verified, obligatorio antes de confiar
    // en el email como identificador de cuenta.
    @Override
    public UsuarioDTO loginConGoogle(String idTokenString) {
        GoogleIdToken idToken;
        try {
            idToken = googleIdTokenVerifier.verify(idTokenString);
        } catch (Exception e) {
            log.warn("Falló la verificación de un ID token de Google: {}", e.getMessage());
            throw new CredencialesInvalidasException("Token de Google inválido");
        }

        if (idToken == null) {
            throw new CredencialesInvalidasException("Token de Google inválido");
        }

        GoogleIdToken.Payload payload = idToken.getPayload();
        Boolean emailVerificado = payload.getEmailVerified();
        if (emailVerificado == null || !emailVerificado) {
            // Nunca se confía en un email que Google no verificó: si no,
            // cualquiera podría loguearse con un mail que no controla.
            throw new CredencialesInvalidasException("El email de la cuenta de Google no está verificado");
        }

        String email = payload.getEmail();
        String nombreEnPayload = (String) payload.get("name");
        if (nombreEnPayload == null || nombreEnPayload.isBlank()) {
            nombreEnPayload = (String) payload.get("given_name");
        }
        final String name = nombreEnPayload;

        // Si ya existe una cuenta con ese mail (creada por registro normal,
        // con password real, o por un login de Google anterior) se reusa:
        // el mail ya está verificado por Google, es razonable enlazar por
        // mail en vez de crear una segunda cuenta duplicada.
        Usuario usuario = repo.findByMail(email).orElseGet(() -> crearUsuarioDesdeGoogle(email, name));

        String token = jwtUtil.generateToken(usuario.getId(), usuario.getMail());
        UsuarioDTO dto = Mapper.toDto(usuario);
        dto.setToken(token);
        return dto;
    }

    private Usuario crearUsuarioDesdeGoogle(String email, String name) {
        // Password aleatoria de alta entropía (UUID v4, 122 bits) hasheada
        // con el mismo encoder que el registro normal: la cuenta existe y
        // tiene una fila de password válida, pero nadie puede loguearse con
        // ella por /usuarios/login adivinando nada, porque no se le entrega
        // a nadie, ni siquiera al propio usuario.
        String passwordAleatoria = UUID.randomUUID().toString() + UUID.randomUUID();
        Usuario nuevo = Usuario.builder()
                .name(name)
                .mail(email)
                .password(passwordEncoder.encode(passwordAleatoria))
                .rol(Rol.USER)
                .username(null)
                .build();
        return repo.save(nuevo);
    }

    @Override
    public UsuarioDTO actualizarUsername(Long userId, String username) {
        if (username == null || username.isBlank()) {
            throw new SolicitudInvalidaException("El username no puede estar vacío");
        }
        String normalizado = username.trim();

        if (repo.existsByUsernameAndIdNot(normalizado, userId)) {
            throw new ConflictoException("El username ya está en uso");
        }

        Usuario usuario = traerUsuarioEntity(userId);
        usuario.setUsername(normalizado);
        return Mapper.toDto(repo.save(usuario));
    }
}
