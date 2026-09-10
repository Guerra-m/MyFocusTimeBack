package com.guerram.MyFocusTime.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RankingDTO {
    private String username;
    private String name;
    private double horasTotales;
    private boolean esUsuarioActual;
}
