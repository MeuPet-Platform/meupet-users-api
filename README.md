# Microsserviço: API de Usuários (meupet-users-api)

![Java](https://img.shields.io/badge/Java-21-blue)
![Quarkus](https://img.shields.io/badge/Quarkus-3.14-blueviolet)
![Maven](https://img.shields.io/badge/Maven-3.9-red)

API central da plataforma MeuPet, responsável pelo gerenciamento completo de usuários e pela autenticação via tokens JWT.

---

## 🚀 Sobre o Serviço

Este microsserviço é o pilar da autenticação e identidade na plataforma. Suas responsabilidades incluem:
-   Cadastro de novos usuários.
-   Autenticação via e-mail e senha.
-   Geração de tokens de segurança JWT para autorização em outros serviços.
-   Operações de CRUD (Criar, Ler, Atualizar, Deletar) para os dados dos usuários.
-   Orquestração da exclusão em cascata de dados associados (como animais) ao deletar um usuário.

### Arquitetura e Comunicação

Este serviço atua tanto como um provedor de identidade quanto como um orquestrador para certas operações. Ele utiliza:
-   **SmallRye JWT:** Para gerar tokens seguros após o login.
-   **Quarkus REST Client:** Para se comunicar com o `meupet-animals-api` e solicitar a exclusão de animais quando um tutor é removido.

---

## 🛠️ Tecnologias Utilizadas

-   **Framework:** Quarkus
-   **Linguagem:** Java 21
-   **Persistência:** Hibernate ORM com Panache
-   **Banco de Dados:** MySQL (Conexão compartilhada)
-   **Segurança:** Quarkus Security (Bcrypt) e SmallRye JWT
-   **Build:** Maven

---

## 📋 Pré-requisitos

-   [Java (GraalVM) 21+](https://www.graalvm.org/downloads/)
-   [Apache Maven 3.9+](https://maven.apache.org/download.cgi)
-   [Docker](https://www.docker.com/products/docker-desktop/) e Docker Compose

---

## ▶️ Como Executar (Desenvolvimento)

Para rodar este serviço em modo de desenvolvimento, a infraestrutura (MySQL) precisa estar rodando primeiro.

1.  **Inicie a Infraestrutura:**
    Navegue até o repositório `meupet-infra` e execute:
    ```bash
    docker-compose up -d
    ```

2.  **Clone e Execute este Serviço:**
    Em um novo terminal, clone e inicie o serviço de usuários.
    ```bash
    git clone [https://github.com/MeuPet-Platform/meupet-users-api.git](https://github.com/MeuPet-Platform/meupet-users-api.git)
    cd meupet-users-api
    mvn quarkus:dev
    ```
    O serviço estará disponível em `http://localhost:8082`.

---

## 🐳 Como Executar (Plataforma Completa com Docker)

Para executar este serviço como parte da plataforma completa, vá para a raiz do repositório `meupet-infra` e execute o comando que orquestra todos os serviços definidos no `docker-compose.yml` da infraestrutura.
```bash
docker-compose up --build
```
*Este comando irá construir a imagem Docker deste serviço e orquestrar a inicialização de toda a plataforma.*

---

## 📖 Endpoints da API

A documentação completa e interativa da API está disponível via Swagger UI enquanto o serviço estiver rodando.

* **URL da Documentação:** [http://localhost:8082/q/swagger-ui/](http://localhost:8082/q/swagger-ui/)

#### Principais Endpoints:
-   `POST /usuarios`: Cadastra um novo usuário.
-   `POST /usuarios/login`: Realiza a autenticação e retorna um token JWT.
-   `GET /usuarios`: Lista todos os usuários (requer permissão de Admin).
-   `GET /usuarios/{id}`: Busca um usuário por ID.
-   `PUT /usuarios/{id}`: Atualiza os dados de um usuário.
-   `DELETE /usuarios/{id}`: Deleta um usuário e todos os seus dados associados (animais, vacinas, etc.).

*Com exceção do cadastro e login, todos os endpoints são protegidos e exigem um token JWT válido.*

---

## ⚙️ Variáveis de Ambiente

Para rodar a imagem Docker em produção, as seguintes variáveis de ambiente são configuradas no `docker-compose.yml`:

-   `QUARKUS_DATASOURCE_JDBC_URL`: A URL de conexão com o banco de dados.
    -   *Exemplo:* `jdbc:mysql://mysql:3306/meupet`
-   `ANIMALS_API_URL`: A URL base do serviço de animais, usada pelo REST Client.
    -   *Exemplo:* `http://animals-api:8083`
