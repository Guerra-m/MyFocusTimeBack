package com.guerram.MyFocusTime.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Amistad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Usuario solicitante;

    @ManyToOne
    private Usuario receptor;

    @Enumerated(EnumType.STRING)
    private EstadoAmistad estado;

    private LocalDateTime creado;
    private LocalDateTime actualizado;
}
