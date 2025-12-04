package com.guerram.MyFocusTime.dto;

import java.time.LocalDate;

public class TiempoEstudioDiaDTO {
    private Long id;
    private LocalDate fecha;
    private double minutosEstudiados;
    private Long usuarioId; //id del usuario
}
