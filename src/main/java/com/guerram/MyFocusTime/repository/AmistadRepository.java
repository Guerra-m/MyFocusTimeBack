package com.guerram.MyFocusTime.repository;

import com.guerram.MyFocusTime.model.Amistad;
import com.guerram.MyFocusTime.model.EstadoAmistad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, Long> {

    boolean existsBySolicitanteIdAndReceptorIdOrSolicitanteIdAndReceptorId(
            Long solicitanteId1, Long receptorId1,
            Long solicitanteId2, Long receptorId2);

    List<Amistad> findByReceptorIdAndEstado(Long receptorId, EstadoAmistad estado);

    @Query("SELECT a FROM Amistad a WHERE (a.solicitante.id = :userId OR a.receptor.id = :userId) " +
            "AND a.estado = com.guerram.MyFocusTime.model.EstadoAmistad.ACEPTADA")
    List<Amistad> findAceptadasByUsuario(@Param("userId") Long userId);
}
