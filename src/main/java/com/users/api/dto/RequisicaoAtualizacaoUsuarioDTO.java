package com.users.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequisicaoAtualizacaoUsuarioDTO {
    @NotBlank(message = "O nome não pode estar em branco.")
    private String nome;
}