package com.guerram.MyFocusTime.dto;

import com.guerram.MyFocusTime.model.Rol;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String name;
    private String mail;
    private Rol rol;
    private String token;
}
