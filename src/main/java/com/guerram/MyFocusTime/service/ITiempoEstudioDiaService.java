package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;

public interface ITiempoEstudioDiaService {
    TiempoEstudioDiaDTO traerTiempo(Long id);
    TiempoEstudioDiaDTO crearTiempo(TiempoEstudioDiaDTO dto);
    TiempoEstudioDiaDTO actualizarTiempo(Long id, TiempoEstudioDiaDTO dto);
}
