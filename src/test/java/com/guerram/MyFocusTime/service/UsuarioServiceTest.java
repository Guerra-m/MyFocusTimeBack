package com.guerram.MyFocusTime.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.guerram.MyFocusTime.config.JwtUtil;
import com.guerram.MyFocusTime.exception.ConflictoException;
import com.guerram.MyFocusTime.exception.CredencialesInvalidasException;
import com.guerram.MyFocusTime.exception.RecursoNoEncontradoException;
import com.guerram.MyFocusTime.exception.SolicitudInvalidaException;
import com.guerram.MyFocusTime.model.Rol;
import com.guerram.MyFocusTime.model.Usuario;
import com.guerram.MyFocusTime.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.GeneralSecurityException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    private UsuarioRepository repo;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private GoogleIdTokenVerifier googleIdTokenVerifier;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        repo = mock(UsuarioRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtUtil = mock(JwtUtil.class);
        googleIdTokenVerifier = mock(GoogleIdTokenVerifier.class);

        service = new UsuarioService();
        ReflectionTestUtils.setField(service, "repo", repo);
        ReflectionTestUtils.setField(service, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(service, "jwtUtil", jwtUtil);
        ReflectionTestUtils.setField(service, "googleIdTokenVerifier", googleIdTokenVerifier);
    }

    private GoogleIdToken tokenValidoCon(String email, boolean emailVerificado, String name) throws Exception {
        GoogleIdToken.Payload payload = new GoogleIdToken.Payload();
        payload.setEmail(email);
        payload.setEmailVerified(emailVerificado);
        payload.set("name", name);

        GoogleIdToken idToken = mock(GoogleIdToken.class);
        when(idToken.getPayload()).thenReturn(payload);
        return idToken;
    }

    @Test
    void login_conMailInexistente_yConPasswordIncorrecta_devuelvenElMismoMensaje() {
        // Mail que no existe
        when(repo.findByMail("nadie@ejemplo.com")).thenReturn(Optional.empty());

        CredencialesInvalidasException porMail = assertThrows(
                CredencialesInvalidasException.class,
                () -> service.login("nadie@ejemplo.com", "loQueSea")
        );

        // Mail que sí existe, pero contraseña incorrecta
        Usuario existente = Usuario.builder().id(1L).mail("real@ejemplo.com").password("hash").build();
        when(repo.findByMail("real@ejemplo.com")).thenReturn(Optional.of(existente));
        when(passwordEncoder.matches("incorrecta", "hash")).thenReturn(false);

        CredencialesInvalidasException porPassword = assertThrows(
                CredencialesInvalidasException.class,
                () -> service.login("real@ejemplo.com", "incorrecta")
        );

        // Si los mensajes difirieran, un atacante podría enumerar qué correos
        // están registrados probando el login.
        assertEquals(porMail.getMessage(), porPassword.getMessage());
    }

    @Test
    void traerUsuario_cuandoNoExiste_lanzaRecursoNoEncontrado() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.traerUsuario(99L));
    }

    @Test
    void actualizarUsername_conUsernameLibre_loGuardaYLoDevuelve() {
        Usuario usuario = Usuario.builder().id(1L).name("Marti").mail("m@m.com").build();
        when(repo.findById(1L)).thenReturn(Optional.of(usuario));
        when(repo.existsByUsernameAndIdNot("marti123", 1L)).thenReturn(false);
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = service.actualizarUsername(1L, "marti123");

        assertEquals("marti123", resultado.getUsername());
        verify(repo).save(argThat(u -> "marti123".equals(u.getUsername())));
    }

    @Test
    void actualizarUsername_conUsernameYaTomadoPorOtroUsuario_lanzaConflicto() {
        when(repo.existsByUsernameAndIdNot("tomado", 1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.actualizarUsername(1L, "tomado"));

        verify(repo, never()).save(any());
    }

    @Test
    void actualizarUsername_conUsernameVacio_lanzaSolicitudInvalida() {
        assertThrows(SolicitudInvalidaException.class, () -> service.actualizarUsername(1L, "   "));

        verify(repo, never()).save(any());
    }

    @Test
    void loginConGoogle_conTokenValidoYUsuarioExistente_devuelveElUsuarioConToken() throws Exception {
        GoogleIdToken idToken = tokenValidoCon("real@ejemplo.com", true, "Marti");
        when(googleIdTokenVerifier.verify("token-valido")).thenReturn(idToken);

        Usuario existente = Usuario.builder()
                .id(1L).mail("real@ejemplo.com").name("Marti").password("hash-viejo").rol(Rol.USER)
                .build();
        when(repo.findByMail("real@ejemplo.com")).thenReturn(Optional.of(existente));
        when(jwtUtil.generateToken(1L, "real@ejemplo.com")).thenReturn("jwt-emitido");

        var resultado = service.loginConGoogle("token-valido");

        assertEquals("real@ejemplo.com", resultado.getMail());
        assertEquals("jwt-emitido", resultado.getToken());
        // No se crea una cuenta nueva: se reusa la existente aunque su
        // password sea el de un registro normal (el mail ya lo verificó Google).
        verify(repo, never()).save(any());
    }

    @Test
    void loginConGoogle_conTokenValidoYUsuarioNuevo_creaElUsuarioYDevuelveToken() throws Exception {
        GoogleIdToken idToken = tokenValidoCon("nuevo@ejemplo.com", true, "Nueva Persona");
        when(googleIdTokenVerifier.verify("token-valido")).thenReturn(idToken);
        when(repo.findByMail("nuevo@ejemplo.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hash-random");
        when(repo.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(42L);
            return u;
        });
        when(jwtUtil.generateToken(42L, "nuevo@ejemplo.com")).thenReturn("jwt-emitido");

        var resultado = service.loginConGoogle("token-valido");

        assertEquals("nuevo@ejemplo.com", resultado.getMail());
        assertEquals("jwt-emitido", resultado.getToken());
        assertNull(resultado.getUsername());

        verify(repo).save(argThat(u ->
                "nuevo@ejemplo.com".equals(u.getMail())
                        && "Nueva Persona".equals(u.getName())
                        && u.getUsername() == null
                        && u.getRol() == Rol.USER
                        && "hash-random".equals(u.getPassword())
        ));
        // La contraseña generada nunca se hashea con un valor fijo o vacío:
        // se le pasa al encoder un valor aleatorio distinto por usuario.
        verify(passwordEncoder).encode(argThat(pw -> pw != null && pw.length() >= 20));
    }

    @Test
    void loginConGoogle_conTokenInvalido_lanzaCredencialesInvalidas() throws Exception {
        when(googleIdTokenVerifier.verify("token-malo")).thenReturn(null);

        assertThrows(CredencialesInvalidasException.class, () -> service.loginConGoogle("token-malo"));
        verify(repo, never()).findByMail(anyString());
    }

    @Test
    void loginConGoogle_conFallaDeVerificacion_lanzaCredencialesInvalidas() throws Exception {
        when(googleIdTokenVerifier.verify("token-roto")).thenThrow(new GeneralSecurityException("firma inválida"));

        assertThrows(CredencialesInvalidasException.class, () -> service.loginConGoogle("token-roto"));
        verify(repo, never()).findByMail(anyString());
    }

    @Test
    void loginConGoogle_conEmailNoVerificado_lanzaCredencialesInvalidas() throws Exception {
        GoogleIdToken idToken = tokenValidoCon("sospechoso@ejemplo.com", false, "Alguien");
        when(googleIdTokenVerifier.verify("token-no-verificado")).thenReturn(idToken);

        assertThrows(CredencialesInvalidasException.class, () -> service.loginConGoogle("token-no-verificado"));
        // Nunca se busca ni se crea una cuenta con un email que Google no verificó:
        // cualquiera podría loguearse con un mail que no controla.
        verify(repo, never()).findByMail(anyString());
        verify(repo, never()).save(any());
    }
}
