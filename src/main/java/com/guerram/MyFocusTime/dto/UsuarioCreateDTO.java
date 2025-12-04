package com.guerram.MyFocusTime.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsuarioCreateDTO {
    private String name;
    private String mail;
    private String password;
}
