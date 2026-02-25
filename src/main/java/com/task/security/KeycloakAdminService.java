package com.task.security;

import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.task.model.Authorities;
import com.task.model.Librarian;

import java.util.Collections;
import java.util.List;

@Service
public class KeycloakAdminService {

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    private Keycloak buildAdminClient() {
        return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm("master")
            .clientId("admin-cli")
            .username(adminUsername)
            .password(adminPassword)
            .build();
    }

    private void createRoleIfNotExists(Keycloak keycloak, String roleName) {
        try {
            keycloak.realm(realm).roles().get(roleName).toRepresentation();
        } catch (Exception e) {
            RoleRepresentation newRole = new RoleRepresentation();
            newRole.setName(roleName);
            newRole.setDescription("Auto created: " + roleName);
            keycloak.realm(realm).roles().create(newRole);
        }
    }

    public boolean createUserInKeycloak(Librarian librarian, String rawPassword) {
        Keycloak keycloak = buildAdminClient();

        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(librarian.getUserName());
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setRequiredActions(Collections.emptyList());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(rawPassword);
        credential.setTemporary(false);
        newUser.setCredentials(Collections.singletonList(credential));

        Response response = keycloak.realm(realm).users().create(newUser);

        if (response.getStatus() != 201) {
            return false;
        }

        String keycloakUserId = response.getLocation()
            .getPath()
            .replaceAll(".*/([^/]+)$", "$1");

        List<Authorities> authorities = librarian.getAuthorities();

        if (authorities != null && !authorities.isEmpty()) {
            for (Authorities authority : authorities) {
                String roleName = authority.getAuthoritieType().toUpperCase();

                createRoleIfNotExists(keycloak, roleName);

                try {
                    RoleRepresentation role = keycloak.realm(realm)
                        .roles()
                        .get(roleName)
                        .toRepresentation();

                    keycloak.realm(realm)
                        .users()
                        .get(keycloakUserId)
                        .roles()
                        .realmLevel()
                        .add(List.of(role));

                    System.out.println("Role assigned: " + roleName);
                } catch (Exception e) {
                    System.err.println("Role assignment failed: " + e.getMessage());
                }
            }
        }

        return true;
    }

    public String loginAndGetToken(String username, String password) {
        try {
            Keycloak userSession = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(username)
                .password(password)
                .grantType("password")
                .build();

            return userSession.tokenManager().getAccessTokenString();
        } catch (Exception e) {
            return null;
        }
    }
}