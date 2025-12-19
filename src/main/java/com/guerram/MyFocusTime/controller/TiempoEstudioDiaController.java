package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;
import com.guerram.MyFocusTime.service.ITiempoEstudioDiaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tiempo")
public class TiempoEstudioDiaController {

    @Autowired
    private ITiempoEstudioDiaService service;

    // Crear registro diario

    @PostMapping("/crear")
    public ResponseEntity<?> crearTiempo(
            @RequestBody TiempoEstudioDiaDTO dto,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        service.crearTiempo(userId, dto);

        return ResponseEntity.ok("Tiempo guardado");
    }

    @GetMapping("/semanal")
    public List<TiempoEstudioDiaDTO> traerTiempoSemanal(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Long userId = (Long) request.getAttribute("userId");

        return service.traerTiempoSemanal(userId, fecha);
    }



    // Tiempo mensual
    @GetMapping("/mensual")
    public List<TiempoEstudioDiaDTO> traerTiempoMes(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Long userId = (Long) request.getAttribute("userId");
        return service.traerTiempoMes(userId, fecha);
    }

    // Tiempo anual
    @GetMapping("/anual")
    public List<TiempoEstudioDiaDTO> traerTiempoAnio(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Long userId = (Long) request.getAttribute("userId");
        return service.traerTiempoAnio(userId, fecha);
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable Long id,
            @RequestBody TiempoEstudioDiaDTO dto,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        service.actualizarTiempo(userId, id, dto);

        return ResponseEntity.ok("Actualizado correctamente");
    }

    /*Llamada desde el front
    Semana
    GET http://localhost:8080/tiempo/semanal/1?fecha=2025-02-03
    Mes
    GET http://localhost:8080/tiempo/mensual/1?fecha=2025-02-03
    Año
    GET http://localhost:8080/tiempo/anual/1?fecha=2025-02-03
     */

}
