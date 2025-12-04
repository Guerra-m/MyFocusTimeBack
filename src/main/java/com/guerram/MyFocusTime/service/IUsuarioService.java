package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.UsuarioDTO;


public interface IUsuarioService {
    UsuarioDTO crearUsuario(UsuarioDTO dto);
    UsuarioDTO traerUsuario(Long id);
}
