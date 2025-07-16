package com.users.api;

import com.users.api.dto.RequisicaoUsuarioDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RequisicaoUsuarioDTOTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private static Stream<RequisicaoUsuarioDTO> casosDeUsuarioInvalidos() {
        return Stream.of(
                criarDTO("", "valido@email.com", "senha123"),      // Nome em branco
                criarDTO("Nome Valido", null, "senha123"),           // E-mail nulo
                criarDTO("Nome Valido", "email-invalido", "senha123"),// E-mail em formato inválido
                criarDTO("Nome Valido", "valido@email.com", " ")   // Senha em branco
        );
    }

    @ParameterizedTest
    @MethodSource("casosDeUsuarioInvalidos")
    void deveDetectarCamposInvalidos(RequisicaoUsuarioDTO dto) {
        Set<ConstraintViolation<RequisicaoUsuarioDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Deveria ter encontrado violações de validação.");
    }

    @Test
    void deveValidarUsuarioComDadosCorretos() {
        RequisicaoUsuarioDTO dto = criarDTO("Nome Valido", "valido@email.com", "senha123");
        Set<ConstraintViolation<RequisicaoUsuarioDTO>> violations = validator.validate(dto);
        assertEquals(0, violations.size(), "Não deveria encontrar violações para um DTO válido.");
    }

    private static RequisicaoUsuarioDTO criarDTO(String nome, String email, String senha) {
        RequisicaoUsuarioDTO dto = new RequisicaoUsuarioDTO();
        dto.setNome(nome);
        dto.setEmail(email);
        dto.setSenha(senha);
        return dto;
    }
}
