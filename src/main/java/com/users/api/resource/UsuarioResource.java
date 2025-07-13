package com.users.api.resource;

import com.users.api.client.AnimalRestClient;
import com.users.api.dto.*;
import com.users.api.entity.UsuarioEntity;
import com.users.api.mapper.UsuarioMapper;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import io.vertx.core.eventbus.EventBus;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Usuários", description = "Operações para gerenciamento de usuários.")
public class UsuarioResource {

    private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

    @Inject
    @RestClient
    AnimalRestClient animalRestClient;

    @GET
    @Operation(summary = "Listar todos os usuários", description = "Retorna uma lista de todos os usuários cadastrados.")
    @APIResponse(responseCode = "200", description = "Lista de usuários", content = @Content(schema = @Schema(implementation = RespostaUsuarioDTO[].class)))
    @RolesAllowed("ADMIN")
    public List<RespostaUsuarioDTO> listar() {
        List<UsuarioEntity> usuarios = UsuarioEntity.listAll();
        return usuarios.stream()
                .map(mapper::toRespostaDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Path("/login")
    @Operation(summary = "Logar na conta", description = "Faz login na conta do usuário")
    @APIResponse(responseCode = "200", description = "Login com sucesso", content = @Content(schema = @Schema(implementation = RespostaUsuarioDTO.class)))
    @APIResponse(responseCode = "401", description = "Não autorizado")
    public Response login(@Valid RequisicaoLoginDTO loginDTO) {

        UsuarioEntity usuario = UsuarioEntity.find("email", loginDTO.getEmail()).firstResult();

        if (usuario != null && BcryptUtil.matches(loginDTO.getSenha(), usuario.getSenha())) {
            String token = Jwt.issuer("https://meupet.api/issuer")
                    .upn(usuario.email)
                    .subject(String.valueOf(usuario.id))
                    .groups(new HashSet<>(Arrays.asList("USER", "ADMIN"))) // permissões
                    .expiresIn(Duration.ofHours(24))
                    .sign();

            return Response.ok(new RespostaTokenDTO(token)).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("E-mail ou senha inválidos.")
                .build();

    }


    @GET
    @Path("/{id}")
    @Operation(summary = "Buscar usuário por ID", description = "Retorna um usuário específico pelo seu ID.")
    @APIResponse(responseCode = "200", description = "Usuário encontrado", content = @Content(schema = @Schema(implementation = RespostaUsuarioDTO.class)))
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public Response buscarPorId(@PathParam("id") Long id) {
        // A busca é feita na entidade específica
        return UsuarioEntity.findByIdOptional(id)
                .map(UsuarioEntity.class::cast)
                .map(mapper::toRespostaDTO)
                .map(dto -> Response.ok(dto).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Operation(summary = "Criar novo usuário", description = "Cadastra um novo usuário no sistema.")
    @APIResponse(responseCode = "201", description = "Usuário criado com sucesso", content = @Content(schema = @Schema(implementation = RespostaUsuarioDTO.class)))
    public Response criar(@Valid RequisicaoUsuarioDTO dto) {
        UsuarioEntity entity = mapper.toEntity(dto);

        String senhaHasheada = BcryptUtil.bcryptHash(dto.getSenha());

        entity.setSenha(senhaHasheada);

        entity.persist();
        return Response.status(Response.Status.CREATED).entity(mapper.toRespostaDTO(entity)).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    @Operation(summary = "Atualizar usuário", description = "Atualiza os dados de um usuário existente.")
    @APIResponse(responseCode = "200", description = "Usuário atualizado com sucesso", content = @Content(schema = @Schema(implementation = RespostaUsuarioDTO.class)))
    @APIResponse(responseCode = "404", description = "Usuário não encontrado")
    public Response atualizar(@PathParam("id") Long id, RequisicaoAtualizacaoUsuarioDTO dto) {
        return UsuarioEntity.<UsuarioEntity>findByIdOptional(id)
                .map(entity -> {
                    mapper.updateEntityFromDTO(dto, entity);
                    entity.persist();
                    return Response.ok(mapper.toRespostaDTO(entity)).build();
                })
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    @RolesAllowed("ADMIN")
    public Response deletar(@PathParam("id") Long id) {
        // Verifica se o usuário existe
        UsuarioEntity usuario = UsuarioEntity.findById(id);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        // 1. Pede para o animals-api deletar os animais do tutor
        Response resposta = animalRestClient.deletarAnimaisPorTutor(id);
        if (resposta.getStatus() != 204) {
            return Response.status(Response.Status.BAD_GATEWAY)
                    .entity("Falha ao excluir animais do tutor.").build();
        }

        // 2. Deleta o usuário
        usuario.delete();

        return Response.noContent().build();
    }


}