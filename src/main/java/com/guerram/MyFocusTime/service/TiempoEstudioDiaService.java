package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;
import com.guerram.MyFocusTime.repository.TiempoEstudioDiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiempoEstudioDiaService implements ITiempoEstudioDiaService {
    @Autowired
    TiempoEstudioDiaRepository repo;

    @Override
    public TiempoEstudioDiaDTO crearTiempo(TiempoEstudioDiaDTO dto) {

        return null;
    }

    @Override
    public TiempoEstudioDiaDTO actualizarTiempo(Long id, TiempoEstudioDiaDTO dto) {

        return null;
    }

    @Override
    public TiempoEstudioDiaDTO traerTiempoSemanal(Long idUsuario, int fecha) {
        return null;
    }

    @Override
    public TiempoEstudioDiaDTO traerTiempoMes(Long idUsuario, int fecha) {
        return null;
    }

    @Override
    public TiempoEstudioDiaDTO traerTiempoAnio(Long idUsuario, int fecha) {
        return null;
    }

}
