package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.GoogleLoginDTO;
import com.guerram.MyFocusTime.dto.UsernameUpdateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.service.IUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Test
    void getUsuario_cuandoElIdCoincideConElUsuarioAutenticado_devuelveElUsuario() {
        IUsuarioService service = mock(IUsuarioService.class);
        UsuarioDTO esperado = UsuarioDTO.builder().id(1L).mail("a@a.com").build();
        when(service.traerUsuario(1L)).thenReturn(esperado);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        UsuarioController controller = new UsuarioController(service);

        UsuarioDTO resultado = controller.getUsuario(1L, request);

        assertEquals(esperado, resultado);
    }

    @Test
    void getUsuario_cuandoElIdNoCoincideConElUsuarioAutenticado_rechazaConForbidden() {
        IUsuarioService service = mock(IUsuarioService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        UsuarioController controller = new UsuarioController(service);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> controller.getUsuario(2L, request));

        assertEquals(403, ex.getStatusCode().value());
        verify(service, never()).traerUsuario(anyLong());
    }

    @Test
    void getMe_conUsuarioAutenticado_devuelveSuPropioPerfil() {
        IUsuarioService service = mock(IUsuarioService.class);
        UsuarioDTO esperado = UsuarioDTO.builder().id(1L).mail("a@a.com").username("marti").build();
        when(service.traerUsuario(1L)).thenReturn(esperado);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        UsuarioController controller = new UsuarioController(service);

        assertEquals(esperado, controller.getMe(request));
    }

    @Test
    void getMe_sinAutenticar_rechazaConUnauthorized() {
        IUsuarioService service = mock(IUsuarioService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(null);

        UsuarioController controller = new UsuarioController(service);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> controller.getMe(request));
        assertEquals(401, ex.getStatusCode().value());
    }

    @Test
    void actualizarUsername_conUsuarioAutenticado_delegaEnElServicioConSuPropioId() {
        IUsuarioService service = mock(IUsuarioService.class);
        UsuarioDTO esperado = UsuarioDTO.builder().id(1L).username("nuevo").build();
        when(service.actualizarUsername(1L, "nuevo")).thenReturn(esperado);

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn(1L);

        UsuarioController controller = new UsuarioController(service);
        UsernameUpdateDTO dto = UsernameUpdateDTO.builder().username("nuevo").build();

        assertEquals(esperado, controller.actualizarUsername(dto, request));
    }

    @Test
    void loginConGoogle_delegaEnElServicioConElIdTokenRecibido() {
        IUsuarioService service = mock(IUsuarioService.class);
        UsuarioDTO esperado = UsuarioDTO.builder().id(5L).mail("g@g.com").token("jwt").build();
        when(service.loginConGoogle("id-token-de-google")).thenReturn(esperado);

        UsuarioController controller = new UsuarioController(service);
        GoogleLoginDTO dto = GoogleLoginDTO.builder().idToken("id-token-de-google").build();

        assertEquals(esperado, controller.loginConGoogle(dto));
    }
}
