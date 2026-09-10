package com.guerram.MyFocusTime.repository;

import com.guerram.MyFocusTime.model.TiempoEstudioDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TiempoEstudioDiaRepository extends JpaRepository<TiempoEstudioDia, Long> {
    List<TiempoEstudioDia> findByUsuarioIdAndFechaBetween(Long idUsuario, LocalDate inicio, LocalDate fin);

    @Query("SELECT COALESCE(SUM(t.minutosEstudiados), 0) FROM TiempoEstudioDia t WHERE t.usuario.id = :usuarioId")
    Double sumMinutosByUsuarioId(@Param("usuarioId") Long usuarioId);

}
