package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.AmigoDTO;
import com.guerram.MyFocusTime.dto.RankingDTO;
import com.guerram.MyFocusTime.dto.SolicitudPendienteDTO;

import java.util.List;

public interface IAmistadService {
    void enviarSolicitud(Long solicitanteId, String usernameDestino);
    void aceptarSolicitud(Long userId, Long solicitudId);
    void rechazarSolicitud(Long userId, Long solicitudId);
    List<AmigoDTO> listarAmigos(Long userId);
    List<SolicitudPendienteDTO> listarPendientes(Long userId);
    List<RankingDTO> obtenerRanking(Long userId);
}
