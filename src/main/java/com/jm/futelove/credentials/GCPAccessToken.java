package com.jm.futelove.credentials;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@ComponentScan
public class GCPAccessToken {

    @Autowired
    private GoogleCloudProperties cloudProperties;
    /* injetando a classe de configuração */
    private GoogleCredentials credential;

    /*
     * Pega o token apontando para o arquivo JSON direto
     */
    public  GoogleCredentials getCredentialDirectFile() throws IOException {
        String securityKeyFile = cloudProperties.getSecurityKeyFile();
        /* recuperando o arquivo json */
        credential = GoogleCredentials /* objeto resposavel pelas credenciais */
                .fromStream(new ClassPathResource(securityKeyFile).getInputStream()) // recupera o arquivo JSON
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/devstorage.read_only"));
        /* informa o escopo do acesso */
        credential.refreshIfExpired();
        return credential;
        /* retorna o objeto GoogleCredentials com as credenciais nele */
    }
}
