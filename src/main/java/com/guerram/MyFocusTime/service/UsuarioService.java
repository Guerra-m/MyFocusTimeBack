package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements IUsuarioService {
    @Autowired
    private UsuarioRepository repo;

    @Override
    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        return null;
    }

    @Override
    public UsuarioDTO traerUsuario(Long id) {
        return null;
    }
}
