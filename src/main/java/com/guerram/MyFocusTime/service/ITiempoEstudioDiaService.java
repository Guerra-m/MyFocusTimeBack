package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;

import java.time.LocalDate;
import java.util.List;

public interface ITiempoEstudioDiaService {
    TiempoEstudioDiaDTO crearTiempo(TiempoEstudioDiaDTO dto);
    List<TiempoEstudioDiaDTO> traerTiempoSemanal(Long idUsuario, LocalDate fechaReferencia);
    List<TiempoEstudioDiaDTO> traerTiempoMes(Long idUsuario, LocalDate fechaReferencia);
    List<TiempoEstudioDiaDTO> traerTiempoAnio(Long idUsuario, LocalDate fechaReferencia);
    //Analizar si lo voy a usar o no.
    TiempoEstudioDiaDTO actualizarTiempo(Long id, TiempoEstudioDiaDTO dto);
}
