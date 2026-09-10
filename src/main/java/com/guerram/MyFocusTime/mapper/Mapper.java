package com.guerram.MyFocusTime.mapper;

import com.guerram.MyFocusTime.dto.TiempoEstudioDiaDTO;
import com.guerram.MyFocusTime.dto.UsuarioDTO;
import com.guerram.MyFocusTime.model.TiempoEstudioDia;
import com.guerram.MyFocusTime.model.Usuario;

public class Mapper {

    //Mapeo de Usuario a UsuarioDTO
    public static UsuarioDTO toDto(Usuario usuario){
        if (usuario==null) return null;

        return UsuarioDTO.builder()
                .id(usuario.getId())
                .name(usuario.getName())
                .rol(usuario.getRol())
                .mail(usuario.getMail())
                .username(usuario.getUsername())
                .build();
    }

    //Mapeo de TiempoEstudioDia a TiempoEstudioDiaDTO
    public static TiempoEstudioDiaDTO toDto(TiempoEstudioDia entidad){
        if(entidad == null) return null;

        return TiempoEstudioDiaDTO.builder()
                .id(entidad.getId())
                .fecha(entidad.getFecha())
                .minutosEstudiados(entidad.getMinutosEstudiados())
                .build();
    }
}
