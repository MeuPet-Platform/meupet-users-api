package com.users.api.util;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Set;

public class UsuarioTestProfile implements QuarkusTestProfile {
    @Override
    public Set<Class<?>> getEnabledAlternatives() {
        return Set.of(MockAnimalRestClient.class);
    }
}