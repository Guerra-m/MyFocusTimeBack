package com.guerram.MyFocusTime.model;

import jakarta.persistence.Entity;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

public class Usuario {
    private Long id;
    private String name;
    private String mail;
    private String password;
    private Rol rol;
}
