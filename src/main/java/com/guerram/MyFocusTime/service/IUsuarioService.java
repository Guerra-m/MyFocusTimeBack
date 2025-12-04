package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.UsuarioCreateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;


public interface IUsuarioService {
    UsuarioDTO crearUsuario(UsuarioCreateDTO dto);
    UsuarioDTO traerUsuario(Long id);
    UsuarioDTO login (String mail, String password);
}
