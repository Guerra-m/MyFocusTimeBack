package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;

public interface ITiempoEstudioDiaService {
    TiempoEstudioDiaDTO crearTiempo(TiempoEstudioDiaDTO dto);
    TiempoEstudioDiaDTO actualizarTiempo(Long id, TiempoEstudioDiaDTO dto);
    TiempoEstudioDiaDTO traerTiempoSemanal(Long idUsuario, int fecha);
    TiempoEstudioDiaDTO traerTiempoMes(Long idUsuario, int fecha);
    TiempoEstudioDiaDTO traerTiempoAnio(Long idUsuario, int fecha);
}
