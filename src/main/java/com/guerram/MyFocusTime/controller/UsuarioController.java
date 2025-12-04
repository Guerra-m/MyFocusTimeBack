package com.guerram.MyFocusTime.controller;

import com.guerram.MyFocusTime.dto.UsuarioCreateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService service;

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

    // TRAER USUARIO POR ID
    @GetMapping("/{id}")
    public UsuarioDTO getUsuario(@PathVariable Long id) {
        return service.traerUsuario(id);
    }
}