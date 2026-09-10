package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.AmigoDTO;
import com.guerram.MyFocusTime.dto.SolicitudAmigoDTO;
import com.guerram.MyFocusTime.service.IAmistadService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AmistadControllerTest {

    @Test
    void enviarSolicitud_conUsuarioAutenticado_delegaYDevuelveCreated() {
        IAmistadService service = mock(IAmistadService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        AmistadController controller = new AmistadController(service);
        SolicitudAmigoDTO dto = SolicitudAmigoDTO.builder().usernameDestino("ana").build();

        ResponseEntity<Void> respuesta = controller.enviarSolicitud(dto, request);

        assertEquals(201, respuesta.getStatusCode().value());
        verify(service).enviarSolicitud(1L, "ana");
    }

    @Test
    void enviarSolicitud_sinAutenticar_rechazaConUnauthorizedYNoLlamaAlServicio() {
        IAmistadService service = mock(IAmistadService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(null);

        AmistadController controller = new AmistadController(service);
        SolicitudAmigoDTO dto = SolicitudAmigoDTO.builder().usernameDestino("ana").build();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.enviarSolicitud(dto, request));

        assertEquals(401, ex.getStatusCode().value());
        verify(service, never()).enviarSolicitud(any(), any());
    }

    @Test
    void aceptarSolicitud_delegaConElIdDeLaSolicitudYElUsuarioAutenticado() {
        IAmistadService service = mock(IAmistadService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        AmistadController controller = new AmistadController(service);
        controller.aceptarSolicitud(10L, request);

        verify(service).aceptarSolicitud(1L, 10L);
    }

    @Test
    void listarAmigos_devuelveLoQueDevuelveElServicioParaElUsuarioAutenticado() {
        IAmistadService service = mock(IAmistadService.class);
        List<AmigoDTO> esperado = List.of(AmigoDTO.builder().id(2L).username("ana").name("Ana").build());
        when(service.listarAmigos(1L)).thenReturn(esperado);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        AmistadController controller = new AmistadController(service);

        assertEquals(esperado, controller.listarAmigos(request));
    }
}
