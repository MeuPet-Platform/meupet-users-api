package com.users.api.util;

import com.users.api.client.AnimalRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.annotation.Priority;

@Alternative
@Priority(1)
@ApplicationScoped
@RestClient
public class MockAnimalRestClient implements AnimalRestClient {

    @Override
    public Response deletarAnimaisPorTutor(Long idTutor) {
        // Simula que a exclusão foi bem-sucedida (status 204)
        return Response.noContent().build();
    }
}
