# Dockerfile para aplicações Quarkus em modo JVM
# Versão final que inclui 'curl' para os healthchecks do Docker Compose

####
# ESTÁGIO 1: Build da Aplicação (esta parte continua a mesma)
# Usa uma imagem com Maven e Java para compilar o projeto.
####
FROM maven:3.9-eclipse-temurin-21 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml -B package -DskipTests

####
# ESTÁGIO 2: Imagem Final de Produção (AQUI ESTÁ A MUDANÇA)
# Usamos uma imagem base Temurin com JRE, que é baseada em Ubuntu e tem 'apt-get'.
####
FROM eclipse-temurin:21-jre

# Instala o 'curl', que é necessário para o healthcheck do Docker Compose
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Configurações padrão do Quarkus
WORKDIR /work/
COPY --from=build /usr/src/app/target/quarkus-app/ /work/

EXPOSE 8082 8083
USER 1001

CMD ["java", "-Dquarkus.http.host=0.0.0.0", "-jar", "quarkus-run.jar"]
