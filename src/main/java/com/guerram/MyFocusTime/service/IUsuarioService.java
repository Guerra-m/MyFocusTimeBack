package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.UsuarioCreateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.model.Usuario;


public interface IUsuarioService {
    UsuarioDTO crearUsuario(UsuarioCreateDTO dto);
    UsuarioDTO traerUsuario(Long id);
    Usuario traerUsuarioEntity(Long id);
    UsuarioDTO login (String mail, String password);
    UsuarioDTO loginConGoogle(String idToken);
    UsuarioDTO actualizarUsername(Long userId, String username);
}
