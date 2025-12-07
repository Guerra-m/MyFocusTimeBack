package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;
import com.guerram.MyFocusTime.mapper.Mapper;
import com.guerram.MyFocusTime.model.TiempoEstudioDia;
import com.guerram.MyFocusTime.model.Usuario;
import com.guerram.MyFocusTime.repository.TiempoEstudioDiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class TiempoEstudioDiaService implements ITiempoEstudioDiaService {
    @Autowired
    TiempoEstudioDiaRepository repo;
    @Autowired
    private IUsuarioService usuarioService;

    @Override
    public TiempoEstudioDiaDTO crearTiempo(Long userId, TiempoEstudioDiaDTO dto) {

        Usuario usuario = usuarioService.traerUsuarioEntity(userId);

        TiempoEstudioDia entity = TiempoEstudioDia.builder()
                .fecha(dto.getFecha())
                .minutosEstudiados(dto.getMinutosEstudiados())
                .usuario(usuario)
                .build();

        return Mapper.toDto(repo.save(entity));
    }


    @Override
    public List<TiempoEstudioDiaDTO> traerTiempoSemanal(Long idUsuario, LocalDate fechaReferencia) {
        LocalDate lunes = fechaReferencia.with(DayOfWeek.MONDAY);
        LocalDate domingo = fechaReferencia.with(DayOfWeek.SUNDAY);

        return repo.findByUsuarioIdAndFechaBetween(idUsuario, lunes, domingo)
                .stream()
                .map(Mapper::toDto)
                .toList();
    }

    @Override
    public List<TiempoEstudioDiaDTO> traerTiempoMes(Long idUsuario, LocalDate fechaReferencia) {
        LocalDate inicioMes = fechaReferencia.withDayOfMonth(1);
        LocalDate finMes = fechaReferencia.withDayOfMonth(fechaReferencia.lengthOfMonth());

        return repo.findByUsuarioIdAndFechaBetween(idUsuario, inicioMes, finMes)
                .stream()
                .map(Mapper::toDto)
                .toList();
    }

    @Override
    public List<TiempoEstudioDiaDTO> traerTiempoAnio(Long idUsuario, LocalDate fechaReferencia) {
        LocalDate inicioAnio = fechaReferencia.withDayOfYear(1);
        LocalDate finAnio = fechaReferencia.withDayOfYear(fechaReferencia.lengthOfYear());

        return repo.findByUsuarioIdAndFechaBetween(idUsuario, inicioAnio, finAnio)
                .stream()
                .map(Mapper::toDto)
                .toList();
    }

    @Override
    public TiempoEstudioDiaDTO actualizarTiempo(Long userId, Long id, TiempoEstudioDiaDTO dto) {
        TiempoEstudioDia entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        // Validación de seguridad: el registro pertenece al usuario autenticado
        if (!entity.getUsuario().getId().equals(userId)) {
            throw new RuntimeException("No puedes editar registros de otro usuario");
        }

        entity.setFecha(dto.getFecha());
        entity.setMinutosEstudiados(dto.getMinutosEstudiados());

        return Mapper.toDto(repo.save(entity));
    }



}
