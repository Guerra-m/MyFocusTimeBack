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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AmistadService implements IAmistadService {

    @Autowired
    private AmistadRepository amistadRepo;
    @Autowired
    private UsuarioRepository usuarioRepo;
    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private ITiempoEstudioDiaService tiempoService;

    @Override
    public void enviarSolicitud(Long solicitanteId, String usernameDestino) {
        Usuario receptor = usuarioRepo.findByUsername(usernameDestino)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un usuario con ese username"));

        if (receptor.getId().equals(solicitanteId)) {
            throw new SolicitudInvalidaException("No podés enviarte una solicitud de amistad a vos mismo");
        }

        boolean yaExiste = amistadRepo.existsBySolicitanteIdAndReceptorIdOrSolicitanteIdAndReceptorId(
                solicitanteId, receptor.getId(), receptor.getId(), solicitanteId);
        if (yaExiste) {
            throw new ConflictoException("Ya existe una amistad (o solicitud) entre estos usuarios");
        }

        Usuario solicitante = usuarioService.traerUsuarioEntity(solicitanteId);

        LocalDateTime ahora = LocalDateTime.now();
        Amistad amistad = Amistad.builder()
                .solicitante(solicitante)
                .receptor(receptor)
                .estado(EstadoAmistad.PENDIENTE)
                .creado(ahora)
                .actualizado(ahora)
                .build();

        amistadRepo.save(amistad);
    }

    private Amistad buscarPendiente(Long solicitudId) {
        Amistad amistad = amistadRepo.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud de amistad no encontrada"));

        // Autorización antes que estado: un usuario ajeno no debe poder inferir
        // si la solicitud existe/está pendiente por la diferencia entre 403 y 404.
        return amistad;
    }

    @Override
    public void aceptarSolicitud(Long userId, Long solicitudId) {
        Amistad amistad = buscarPendiente(solicitudId);

        if (!amistad.getReceptor().getId().equals(userId)) {
            throw new AccesoDenegadoException("Solo el receptor puede aceptar la solicitud");
        }
        if (amistad.getEstado() != EstadoAmistad.PENDIENTE) {
            throw new RecursoNoEncontradoException("Solicitud de amistad no encontrada");
        }

        amistad.setEstado(EstadoAmistad.ACEPTADA);
        amistad.setActualizado(LocalDateTime.now());
        amistadRepo.save(amistad);
    }

    @Override
    public void rechazarSolicitud(Long userId, Long solicitudId) {
        Amistad amistad = buscarPendiente(solicitudId);

        if (!amistad.getReceptor().getId().equals(userId)) {
            throw new AccesoDenegadoException("Solo el receptor puede rechazar la solicitud");
        }
        if (amistad.getEstado() != EstadoAmistad.PENDIENTE) {
            throw new RecursoNoEncontradoException("Solicitud de amistad no encontrada");
        }

        amistadRepo.delete(amistad);
    }

    @Override
    public List<AmigoDTO> listarAmigos(Long userId) {
        return amistadRepo.findAceptadasByUsuario(userId).stream()
                .map(a -> otroUsuario(a, userId))
                .map(u -> AmigoDTO.builder().id(u.getId()).username(u.getUsername()).name(u.getName()).build())
                .toList();
    }

    @Override
    public List<SolicitudPendienteDTO> listarPendientes(Long userId) {
        return amistadRepo.findByReceptorIdAndEstado(userId, EstadoAmistad.PENDIENTE).stream()
                .map(a -> SolicitudPendienteDTO.builder()
                        .id(a.getId())
                        .solicitanteUsername(a.getSolicitante().getUsername())
                        .solicitanteName(a.getSolicitante().getName())
                        .build())
                .toList();
    }

    @Override
    public List<RankingDTO> obtenerRanking(Long userId) {
        Usuario usuarioActual = usuarioService.traerUsuarioEntity(userId);

        List<Usuario> participantes = new ArrayList<>();
        participantes.add(usuarioActual);
        amistadRepo.findAceptadasByUsuario(userId).forEach(a -> participantes.add(otroUsuario(a, userId)));

        return participantes.stream()
                .map(u -> RankingDTO.builder()
                        .username(u.getUsername() != null ? u.getUsername() : u.getName())
                        .name(u.getName())
                        .horasTotales(tiempoService.traerHorasTotales(u.getId()))
                        .esUsuarioActual(u.getId().equals(userId))
                        .build())
                .sorted(Comparator.comparingDouble(RankingDTO::getHorasTotales).reversed())
                .toList();
    }

    private Usuario otroUsuario(Amistad amistad, Long userId) {
        return amistad.getSolicitante().getId().equals(userId) ? amistad.getReceptor() : amistad.getSolicitante();
    }
}
