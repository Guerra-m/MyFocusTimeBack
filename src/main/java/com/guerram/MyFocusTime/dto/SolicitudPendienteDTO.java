package com.guerram.MyFocusTime.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SolicitudPendienteDTO {
    private Long id;
    private String solicitanteUsername;
    private String solicitanteName;
}
