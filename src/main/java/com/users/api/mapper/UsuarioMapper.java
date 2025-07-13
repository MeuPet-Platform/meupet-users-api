package com.users.api.mapper;

import com.users.api.dto.RequisicaoAtualizacaoUsuarioDTO;
import com.users.api.dto.RequisicaoUsuarioDTO;
import com.users.api.dto.RespostaUsuarioDTO;
import com.users.api.entity.UsuarioEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UsuarioMapper {

    @Mapping(target = "id", ignore = true)
    UsuarioEntity toEntity(RequisicaoUsuarioDTO dto);

    RespostaUsuarioDTO toRespostaDTO(UsuarioEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "senha", ignore = true)
    void updateEntityFromDTO(RequisicaoAtualizacaoUsuarioDTO dto, @MappingTarget UsuarioEntity entity);
}
