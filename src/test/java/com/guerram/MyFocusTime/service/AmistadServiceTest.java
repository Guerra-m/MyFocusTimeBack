package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.AmigoDTO;
import com.guerram.MyFocusTime.dto.RankingDTO;
import com.guerram.MyFocusTime.dto.SolicitudPendienteDTO;
import com.guerram.MyFocusTime.exception.AccesoDenegadoException;
import com.guerram.MyFocusTime.exception.ConflictoException;
import com.guerram.MyFocusTime.exception.RecursoNoEncontradoException;
import com.guerram.MyFocusTime.exception.SolicitudInvalidaException;
import com.guerram.MyFocusTime.model.Amistad;
import com.guerram.MyFocusTime.model.EstadoAmistad;
import com.guerram.MyFocusTime.model.Usuario;
import com.guerram.MyFocusTime.repository.AmistadRepository;
import com.guerram.MyFocusTime.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AmistadServiceTest {

    private AmistadRepository amistadRepo;
    private UsuarioRepository usuarioRepo;
    private IUsuarioService usuarioService;
    private ITiempoEstudioDiaService tiempoService;
    private AmistadService service;

    private final Usuario yo = Usuario.builder().id(1L).name("Marti").username("marti").build();
    private final Usuario otro = Usuario.builder().id(2L).name("Ana").username("ana").build();

    @BeforeEach
    void setUp() {
        amistadRepo = mock(AmistadRepository.class);
        usuarioRepo = mock(UsuarioRepository.class);
        usuarioService = mock(IUsuarioService.class);
        tiempoService = mock(ITiempoEstudioDiaService.class);

        service = new AmistadService();
        ReflectionTestUtils.setField(service, "amistadRepo", amistadRepo);
        ReflectionTestUtils.setField(service, "usuarioRepo", usuarioRepo);
        ReflectionTestUtils.setField(service, "usuarioService", usuarioService);
        ReflectionTestUtils.setField(service, "tiempoService", tiempoService);
    }

    // ---- enviarSolicitud ----

    @Test
    void enviarSolicitud_conUsernameValido_creaAmistadPendiente() {
        when(usuarioRepo.findByUsername("ana")).thenReturn(Optional.of(otro));
        when(amistadRepo.existsBySolicitanteIdAndReceptorIdOrSolicitanteIdAndReceptorId(1L, 2L, 2L, 1L))
                .thenReturn(false);
        when(usuarioService.traerUsuarioEntity(1L)).thenReturn(yo);

        service.enviarSolicitud(1L, "ana");

        verify(amistadRepo).save(argThat(a ->
                a.getSolicitante().getId().equals(1L)
                        && a.getReceptor().getId().equals(2L)
                        && a.getEstado() == EstadoAmistad.PENDIENTE));
    }

    @Test
    void enviarSolicitud_conUsernameInexistente_lanzaRecursoNoEncontrado() {
        when(usuarioRepo.findByUsername("nadie")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.enviarSolicitud(1L, "nadie"));
        verify(amistadRepo, never()).save(any());
    }

    @Test
    void enviarSolicitud_aSiMismo_lanzaSolicitudInvalida() {
        when(usuarioRepo.findByUsername("marti")).thenReturn(Optional.of(yo));

        assertThrows(SolicitudInvalidaException.class, () -> service.enviarSolicitud(1L, "marti"));
        verify(amistadRepo, never()).save(any());
    }

    @Test
    void enviarSolicitud_conAmistadYaExistente_lanzaConflicto() {
        when(usuarioRepo.findByUsername("ana")).thenReturn(Optional.of(otro));
        when(amistadRepo.existsBySolicitanteIdAndReceptorIdOrSolicitanteIdAndReceptorId(1L, 2L, 2L, 1L))
                .thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.enviarSolicitud(1L, "ana"));
        verify(amistadRepo, never()).save(any());
    }

    // ---- aceptarSolicitud ----

    @Test
    void aceptarSolicitud_siendoElReceptor_laMarcaAceptada() {
        Amistad pendiente = Amistad.builder().id(10L).solicitante(otro).receptor(yo).estado(EstadoAmistad.PENDIENTE).build();
        when(amistadRepo.findById(10L)).thenReturn(Optional.of(pendiente));

        service.aceptarSolicitud(1L, 10L);

        verify(amistadRepo).save(argThat(a -> a.getEstado() == EstadoAmistad.ACEPTADA));
    }

    @Test
    void aceptarSolicitud_noSiendoElReceptor_lanzaAccesoDenegado() {
        Amistad pendiente = Amistad.builder().id(10L).solicitante(yo).receptor(otro).estado(EstadoAmistad.PENDIENTE).build();
        when(amistadRepo.findById(10L)).thenReturn(Optional.of(pendiente));

        assertThrows(AccesoDenegadoException.class, () -> service.aceptarSolicitud(1L, 10L));
        verify(amistadRepo, never()).save(any());
    }

    @Test
    void aceptarSolicitud_queNoEstaPendiente_lanzaRecursoNoEncontrado() {
        Amistad aceptada = Amistad.builder().id(10L).solicitante(otro).receptor(yo).estado(EstadoAmistad.ACEPTADA).build();
        when(amistadRepo.findById(10L)).thenReturn(Optional.of(aceptada));

        assertThrows(RecursoNoEncontradoException.class, () -> service.aceptarSolicitud(1L, 10L));
    }

    @Test
    void aceptarSolicitud_queNoExiste_lanzaRecursoNoEncontrado() {
        when(amistadRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> service.aceptarSolicitud(1L, 99L));
    }

    // ---- rechazarSolicitud ----

    @Test
    void rechazarSolicitud_siendoElReceptor_laElimina() {
        Amistad pendiente = Amistad.builder().id(10L).solicitante(otro).receptor(yo).estado(EstadoAmistad.PENDIENTE).build();
        when(amistadRepo.findById(10L)).thenReturn(Optional.of(pendiente));

        service.rechazarSolicitud(1L, 10L);

        verify(amistadRepo).delete(pendiente);
    }

    @Test
    void rechazarSolicitud_noSiendoElReceptor_lanzaAccesoDenegado() {
        Amistad pendiente = Amistad.builder().id(10L).solicitante(yo).receptor(otro).estado(EstadoAmistad.PENDIENTE).build();
        when(amistadRepo.findById(10L)).thenReturn(Optional.of(pendiente));

        assertThrows(AccesoDenegadoException.class, () -> service.rechazarSolicitud(1L, 10L));
        verify(amistadRepo, never()).delete(any());
    }

    // ---- listarAmigos ----

    @Test
    void listarAmigos_devuelveElOtroUsuarioDeCadaAmistadAceptada() {
        Amistad comoSolicitante = Amistad.builder().id(1L).solicitante(yo).receptor(otro).estado(EstadoAmistad.ACEPTADA).build();
        when(amistadRepo.findAceptadasByUsuario(1L)).thenReturn(List.of(comoSolicitante));

        List<AmigoDTO> amigos = service.listarAmigos(1L);

        assertEquals(1, amigos.size());
        assertEquals("ana", amigos.get(0).getUsername());
        assertEquals(2L, amigos.get(0).getId());
    }

    @Test
    void listarAmigos_cuandoSoyElReceptor_devuelveAlSolicitanteComoAmigo() {
        Amistad comoReceptor = Amistad.builder().id(1L).solicitante(otro).receptor(yo).estado(EstadoAmistad.ACEPTADA).build();
        when(amistadRepo.findAceptadasByUsuario(1L)).thenReturn(List.of(comoReceptor));

        List<AmigoDTO> amigos = service.listarAmigos(1L);

        assertEquals(1, amigos.size());
        assertEquals("ana", amigos.get(0).getUsername());
    }

    // ---- listarPendientes ----

    @Test
    void listarPendientes_devuelveLasSolicitudesDondeSoyReceptor() {
        Amistad pendiente = Amistad.builder().id(5L).solicitante(otro).receptor(yo).estado(EstadoAmistad.PENDIENTE).build();
        when(amistadRepo.findByReceptorIdAndEstado(1L, EstadoAmistad.PENDIENTE)).thenReturn(List.of(pendiente));

        List<SolicitudPendienteDTO> pendientes = service.listarPendientes(1L);

        assertEquals(1, pendientes.size());
        assertEquals(5L, pendientes.get(0).getId());
        assertEquals("ana", pendientes.get(0).getSolicitanteUsername());
        assertEquals("Ana", pendientes.get(0).getSolicitanteName());
    }

    // ---- obtenerRanking ----

    @Test
    void obtenerRanking_ordenaDeMayorAMenorHorasYMarcaElUsuarioActual() {
        when(usuarioService.traerUsuarioEntity(1L)).thenReturn(yo);
        Amistad comoSolicitante = Amistad.builder().id(1L).solicitante(yo).receptor(otro).estado(EstadoAmistad.ACEPTADA).build();
        when(amistadRepo.findAceptadasByUsuario(1L)).thenReturn(List.of(comoSolicitante));
        when(tiempoService.traerHorasTotales(1L)).thenReturn(3.0);
        when(tiempoService.traerHorasTotales(2L)).thenReturn(10.0);

        List<RankingDTO> ranking = service.obtenerRanking(1L);

        assertEquals(2, ranking.size());
        assertEquals("ana", ranking.get(0).getUsername());
        assertEquals(10.0, ranking.get(0).getHorasTotales());
        assertFalse(ranking.get(0).isEsUsuarioActual());
        assertEquals("marti", ranking.get(1).getUsername());
        assertEquals(3.0, ranking.get(1).getHorasTotales());
        assertTrue(ranking.get(1).isEsUsuarioActual());
    }

    @Test
    void obtenerRanking_conAmigoSinUsername_usaElNombreComoFallback() {
        Usuario sinUsername = Usuario.builder().id(3L).name("Beto").username(null).build();
        when(usuarioService.traerUsuarioEntity(1L)).thenReturn(yo);
        Amistad amistad = Amistad.builder().id(2L).solicitante(yo).receptor(sinUsername).estado(EstadoAmistad.ACEPTADA).build();
        when(amistadRepo.findAceptadasByUsuario(1L)).thenReturn(List.of(amistad));
        when(tiempoService.traerHorasTotales(anyLong())).thenReturn(0.0);

        List<RankingDTO> ranking = service.obtenerRanking(1L);

        assertTrue(ranking.stream().anyMatch(r -> "Beto".equals(r.getUsername())));
    }
}
