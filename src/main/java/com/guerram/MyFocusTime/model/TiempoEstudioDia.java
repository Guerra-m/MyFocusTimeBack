package com.guerram.MyFocusTime.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

public class TiempoEstudioDia {
    private Long id;
    private LocalDate fecha;
    private double minutosEstudiados;

    @ManyToOne
    private Usuario usuario;
}
