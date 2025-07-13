package com.users.api.client;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/animais")
@RegisterRestClient(configKey = "animals-api")
public interface AnimalRestClient {

    @DELETE
    @Path("/por-tutor/{idTutor}")
    Response deletarAnimaisPorTutor(@PathParam("idTutor") Long idTutor);
}
