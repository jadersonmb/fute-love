package com.jm.futelove;

import com.google.auth.oauth2.GoogleCredentials;
import com.jm.futelove.credentials.GCPAccessToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
@AutoConfigureMockMvc
public class GcpAuthApplicationTests {
    @Autowired
    GCPAccessToken gcpAccessToken;

    @Test
    public void returnToken() throws IOException {
        GoogleCredentials credentialDirectFile = gcpAccessToken.getCredentialDirectFile();
        /* instanciando o GoogleCredentials e chamando nosso metodo que usa o JSON para se autenticar */
        String accessToken = credentialDirectFile.getAccessToken().toString();
        /* Salvando o conteudo do AccessToken em uma String */
        System.out.printf("AccessToken: %s", accessToken);
        /* Imprime na tela o valor do Token */
    }
}