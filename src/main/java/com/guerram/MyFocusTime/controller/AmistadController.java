package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.AmigoDTO;
import com.guerram.MyFocusTime.dto.RankingDTO;
import com.guerram.MyFocusTime.dto.SolicitudAmigoDTO;
import com.guerram.MyFocusTime.dto.SolicitudPendienteDTO;
import com.guerram.MyFocusTime.service.IAmistadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/amigos")
public class AmistadController {

    private final IAmistadService service;

    public AmistadController(IAmistadService service) {
        this.service = service;
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<Void> enviarSolicitud(@RequestBody SolicitudAmigoDTO dto, HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        service.enviarSolicitud(userId, dto.getUsernameDestino());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/solicitudes/{id}/aceptar")
    public ResponseEntity<Void> aceptarSolicitud(@PathVariable Long id, HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        service.aceptarSolicitud(userId, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<Void> rechazarSolicitud(@PathVariable Long id, HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        service.rechazarSolicitud(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<AmigoDTO> listarAmigos(HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        return service.listarAmigos(userId);
    }

    @GetMapping("/solicitudes/pendientes")
    public List<SolicitudPendienteDTO> listarPendientes(HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        return service.listarPendientes(userId);
    }

    @GetMapping("/ranking")
    public List<RankingDTO> ranking(HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        return service.obtenerRanking(userId);
    }

    private Long usuarioAutenticado(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return userId;
    }
}
