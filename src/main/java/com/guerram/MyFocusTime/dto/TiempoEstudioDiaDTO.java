package com.guerram.MyFocusTime.dto;

import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TiempoEstudioDiaDTO {
    private Long id;
    private LocalDate fecha;
    private double minutosEstudiados;
}
