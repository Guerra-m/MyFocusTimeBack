package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;
import com.guerram.MyFocusTime.service.ITiempoEstudioDiaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
    public TiempoEstudioDiaDTO crearTiempo(@RequestBody TiempoEstudioDiaDTO dto) {
        return service.crearTiempo(dto);
    }

    @GetMapping("/semanal")
    public List<TiempoEstudioDiaDTO> traerTiempoSemanal(
            HttpServletRequest request,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        Long userId = (Long) request.getAttribute("userId");
        return service.traerTiempoSemanal(userId, fecha);
    }


    // Tiempo mensual
    @GetMapping("/mensual/{idUsuario}")
    public List<TiempoEstudioDiaDTO> traerTiempoMes(
            @PathVariable Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return service.traerTiempoMes(idUsuario, fecha);
    }

    // Tiempo anual
    @GetMapping("/anual/{idUsuario}")
    public List<TiempoEstudioDiaDTO> traerTiempoAnio(
            @PathVariable Long idUsuario,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return service.traerTiempoAnio(idUsuario, fecha);
    }
    @PutMapping("/actualizar/{id}")
    public TiempoEstudioDiaDTO actualizarTiempo(
            @PathVariable Long id,
            @RequestBody TiempoEstudioDiaDTO dto) {

        return service.actualizarTiempo(id, dto);
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
