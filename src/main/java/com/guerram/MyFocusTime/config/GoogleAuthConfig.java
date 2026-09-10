package com.guerram.MyFocusTime.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * Verificador de ID tokens de Google (Google Identity Services) usado por
 * POST /usuarios/google. La audiencia se restringe al client-id propio de la
 * app (google.client-id / GOOGLE_CLIENT_ID) para que un token válido emitido
 * para OTRA aplicación que use el mismo proveedor no sea aceptado acá
 * (confusión de audiencia, AUTN-10). GoogleIdTokenVerifier ya valida
 * `iss`, `aud`, `exp` y la firma contra las claves públicas de Google.
 */
@Configuration
public class GoogleAuthConfig {

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier(@Value("${google.client-id}") String googleClientId) {
        return new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
    }
}
