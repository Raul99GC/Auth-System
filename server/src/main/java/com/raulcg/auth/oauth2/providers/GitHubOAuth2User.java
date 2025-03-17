package com.raulcg.auth.oauth2.providers;

import com.raulcg.auth.dtos.OAuthUserRegistrationDTO;
import com.raulcg.auth.enums.Providers;
import com.raulcg.auth.models.User;
import com.raulcg.auth.services.user.IUserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class GitHubOAuth2User implements OAuth2UserStrategy {

    private static final String REGISTRATION_ID = "github";
    private static final String ATTRIBUTE_KEY = "id";
    private final IUserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    public GitHubOAuth2User(IUserService userService, OAuth2AuthorizedClientService authorizedClientService) {
        this.userService = userService;
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public String getRegistrationId() {
        return REGISTRATION_ID;
    }

    @Override
    public String geAttributeKey() {
        return ATTRIBUTE_KEY;
    }

    @Override
    public User processUser(OAuth2AuthenticationToken token) {
        DefaultOAuth2User principal = (DefaultOAuth2User) token.getPrincipal();
        Map<String, Object> attributes = principal.getAttributes();

        // Obtener el email del usuario por attributes o getUserEmails
        String email = (String) attributes.get("email") != null ? (String) attributes.get("email") : getUserEmails(token).getFirst();

        Optional<User> userDb = userService.findByEmail(email);

        if (userDb.isPresent()) {
            if (userDb.get().getProvider().name().equals(REGISTRATION_ID)) {
                return userDb.get();
            }
            return userDb.get();
        }

        String username = attributes.getOrDefault("login", "").toString(); // Obtener el nombre de usuario del usuario

        OAuthUserRegistrationDTO user = new OAuthUserRegistrationDTO();
        user.setEmail(email);
        user.setUsername(username);
        user.setProvider(Providers.GITHUB);
        return userService.createUserByProvider(user);
    }

    public List<String> getUserEmails(OAuth2AuthenticationToken token) {
        List<String> emails = new ArrayList<>();
        try {
            String accessToken = getAccessToken(token);
            // Crear un cliente HTTP
            HttpClient client = HttpClient.newHttpClient();

            // Construir la solicitud GET con los headers necesarios
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/user/emails"))
                    .header("Accept", "application/vnd.github+json")
                    .header("Authorization", "Bearer " + accessToken)
                    .build();

            // Enviar la solicitud y obtener la respuesta como String
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Verificar que la respuesta sea exitosa (código 200)
            if (response.statusCode() == 200) {
                // Parsear el cuerpo de la respuesta (que es un arreglo JSON)
                JSONArray jsonArray = new JSONArray(response.body());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject emailObj = jsonArray.getJSONObject(i);
                    // Se asume que cada objeto tiene la propiedad "email"
                    emails.add(emailObj.getString("email"));
                }
            } else {
                // Manejar posibles errores en la respuesta
                System.err.println("Error en la solicitud: " + response.statusCode());
                System.err.println("Detalle: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName());
        return client.getAccessToken().getTokenValue();
    }
}
