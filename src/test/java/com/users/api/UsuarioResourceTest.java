package com.users.api;

import com.users.api.dto.RequisicaoAtualizacaoUsuarioDTO;
import com.users.api.dto.RequisicaoLoginDTO;
import com.users.api.dto.RequisicaoUsuarioDTO;
import com.users.api.util.UsuarioTestProfile;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestProfile(UsuarioTestProfile.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UsuarioResourceTest {

    private static Long generatedUserId;
    private static String authToken;
    private static final String USER_EMAIL = "teste.completo@exemplo.com";
    private static final String USER_PASSWORD = "senha123";

    @Test
    @Order(1)
    @Transactional
    public void deveCriarUsuario() {
        RequisicaoUsuarioDTO dto = new RequisicaoUsuarioDTO();
        dto.setNome("Usuario de Teste");
        dto.setEmail(USER_EMAIL);
        dto.setSenha(USER_PASSWORD);

        generatedUserId = given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when().post("/usuarios")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");
    }

    @Test
    @Order(2)
    public void deveFazerLoginEObterToken() {
        RequisicaoLoginDTO loginDTO = new RequisicaoLoginDTO();
        loginDTO.setEmail(USER_EMAIL);
        loginDTO.setSenha(USER_PASSWORD);

        authToken = given()
                .contentType(ContentType.JSON)
                .body(loginDTO)
                .when().post("/usuarios/login")
                .then()
                .statusCode(200)
                .extract().path("token");
    }

    @Test
    @Order(3)
    @Transactional
    public void deveAtualizarUsuario() {
        Assumptions.assumeTrue(authToken != null);
        RequisicaoAtualizacaoUsuarioDTO dto = new RequisicaoAtualizacaoUsuarioDTO();
        dto.setNome("Usuario Atualizado");

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(dto)
                .when().put("/usuarios/" + generatedUserId)
                .then()
                .statusCode(200)
                .body("nome", is("Usuario Atualizado"));
    }

    @Test
    @Order(4)
    @Transactional
    public void deveDeletarUsuario() {
        Assumptions.assumeTrue(authToken != null);
        given()
                .header("Authorization", "Bearer " + authToken)
                .when().delete("/usuarios/" + generatedUserId)
                .then()
                .statusCode(204);
    }
}
