package com.guerram.MyFocusTime.repository;

import com.guerram.MyFocusTime.model.TiempoEstudioDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TiempoEstudioDiaRepository extends JpaRepository<TiempoEstudioDiaRepository, Long> {
    List<TiempoEstudioDia> findByUsuarioIdAndFechaBetween(Long idUsuario, LocalDate inicio, LocalDate fin);

}
