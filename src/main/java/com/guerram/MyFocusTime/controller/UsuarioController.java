package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.GoogleLoginDTO;
import com.guerram.MyFocusTime.dto.UsernameUpdateDTO;
import com.guerram.MyFocusTime.dto.UsuarioCreateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.service.IUsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final IUsuarioService service;

    public UsuarioController(IUsuarioService service) {
        this.service = service;
    }

    // REGISTRO
    @PostMapping("/register")
    public UsuarioDTO register(@RequestBody UsuarioCreateDTO dto) {
        return service.crearUsuario(dto);
    }

    // LOGIN
    @PostMapping("/login")
    public UsuarioDTO login(@RequestParam String mail, @RequestParam String password) {
        return service.login(mail, password);
    }

    // LOGIN CON GOOGLE (Google Identity Services: ID token verificado en el backend)
    @PostMapping("/google")
    public UsuarioDTO loginConGoogle(@RequestBody GoogleLoginDTO dto) {
        return service.loginConGoogle(dto.getIdToken());
    }

    // TRAER USUARIO POR ID
    @GetMapping("/{id}")
    public UsuarioDTO getUsuario(@PathVariable Long id, HttpServletRequest request) {
        Long authenticatedUserId = (Long) request.getAttribute("userId");
        if (authenticatedUserId == null || !authenticatedUserId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No podés acceder a datos de otro usuario");
        }
        return service.traerUsuario(id);
    }

    // PERFIL PROPIO
    @GetMapping("/me")
    public UsuarioDTO getMe(HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        return service.traerUsuario(userId);
    }

    // ACTUALIZAR USERNAME PROPIO
    @PutMapping("/me/username")
    public UsuarioDTO actualizarUsername(@RequestBody UsernameUpdateDTO dto, HttpServletRequest request) {
        Long userId = usuarioAutenticado(request);
        return service.actualizarUsername(userId, dto.getUsername());
    }

    private Long usuarioAutenticado(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        return userId;
    }
}