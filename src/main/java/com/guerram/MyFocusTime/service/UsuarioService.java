package com.guerram.MyFocusTime.service;

import com.guerram.MyFocusTime.dto.UsuarioCreateDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.mapper.Mapper;
import com.guerram.MyFocusTime.model.Rol;
import com.guerram.MyFocusTime.model.Usuario;
import com.guerram.MyFocusTime.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService implements IUsuarioService {
    @Autowired
    private UsuarioRepository repo;

    @Override
    public UsuarioDTO crearUsuario(UsuarioCreateDTO dto) {
        Usuario usuario = Usuario.builder()
                .name(dto.getName())
                .mail(dto.getMail())
                .password(dto.getPassword())
                .rol(Rol.USER)
                .build();
        return Mapper.toDto(repo.save(usuario));
    }

    @Override
    public UsuarioDTO traerUsuario(Long id) {
        return repo.findById(id)
                .map(Mapper::toDto)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado."));
    }

    public UsuarioDTO login (String mail, String password) {
        Usuario usuario = repo.findByMail(mail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if(!usuario.getPassword().equals(password)){
            throw new RuntimeException("Contraseña incorrecta");
        }
        return Mapper.toDto(usuario);
    }
}
