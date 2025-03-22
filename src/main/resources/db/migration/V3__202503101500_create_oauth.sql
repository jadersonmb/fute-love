-- Tabela de Clientes OAuth2
CREATE TABLE oauth2_registered_client (
                                          id VARCHAR(100) PRIMARY KEY,
                                          client_id VARCHAR(100) NOT NULL UNIQUE,
                                          client_id_issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          client_secret VARCHAR(200) NOT NULL,
                                          client_secret_expires_at TIMESTAMP NULL DEFAULT NULL,
                                          client_name VARCHAR(200) NOT NULL,
                                          client_authentication_methods VARCHAR(100) NOT NULL,
                                          authorization_grant_types VARCHAR(100) NOT NULL,
                                          redirect_uris TEXT NULL,
                                          post_logout_redirect_uris TEXT NULL,
                                          scopes VARCHAR(500) NOT NULL,
                                          client_settings TEXT NULL,
                                          token_settings TEXT NULL
);

-- Tabela de Autorização (Armazena os tokens emitidos)
CREATE TABLE oauth2_authorization (
                                      id VARCHAR(100) PRIMARY KEY,
                                      registered_client_id VARCHAR(100) NOT NULL,
                                      principal_name VARCHAR(255) NOT NULL,
                                      authorization_grant_type VARCHAR(100) NOT NULL,
                                      authorized_scopes TEXT,
                                      attributes TEXT,
                                      state VARCHAR(500),

                                      authorization_code_value TEXT,
                                      authorization_code_issued_at TIMESTAMP NULL DEFAULT NULL,
                                      authorization_code_expires_at TIMESTAMP NULL DEFAULT NULL,
                                      authorization_code_metadata TEXT,

                                      access_token_value TEXT,
                                      access_token_issued_at TIMESTAMP NULL DEFAULT NULL,
                                      access_token_expires_at TIMESTAMP NULL DEFAULT NULL,
                                      access_token_metadata TEXT,
                                      access_token_type VARCHAR(50),
                                      access_token_scopes TEXT,

                                      oidc_id_token_value TEXT,
                                      oidc_id_token_issued_at TIMESTAMP NULL DEFAULT NULL,
                                      oidc_id_token_expires_at TIMESTAMP NULL DEFAULT NULL,
                                      oidc_id_token_metadata TEXT,

                                      refresh_token_value TEXT,
                                      refresh_token_issued_at TIMESTAMP NULL DEFAULT NULL,
                                      refresh_token_expires_at TIMESTAMP NULL DEFAULT NULL,
                                      refresh_token_metadata TEXT,

                                      user_code_value TEXT,
                                      user_code_issued_at TIMESTAMP NULL DEFAULT NULL,
                                      user_code_expires_at TIMESTAMP NULL DEFAULT NULL,
                                      user_code_metadata TEXT,

                                      device_code_value TEXT,
                                      device_code_issued_at TIMESTAMP NULL DEFAULT NULL,
                                      device_code_expires_at TIMESTAMP NULL DEFAULT NULL,
                                      device_code_metadata TEXT
);


-- Tabela de Aprovações do Usuário
CREATE TABLE oauth2_authorization_consent (
                                              registered_client_id VARCHAR(100) NOT NULL,
                                              principal_name VARCHAR(200) NOT NULL,
                                              authorities TEXT NOT NULL,
                                              PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE SPRING_SESSION (
                                PRIMARY_ID CHAR(36) NOT NULL,
                                SESSION_ID CHAR(36) NOT NULL,
                                CREATION_TIME BIGINT NOT NULL,
                                LAST_ACCESS_TIME BIGINT NOT NULL,
                                MAX_INACTIVE_INTERVAL INT NOT NULL,
                                EXPIRY_TIME BIGINT NOT NULL,
                                PRINCIPAL_NAME VARCHAR(100),
                                PRIMARY KEY (PRIMARY_ID)
);

CREATE UNIQUE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (SESSION_ID);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX3 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
                                           SESSION_PRIMARY_ID CHAR(36) NOT NULL,
                                           ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
                                           ATTRIBUTE_BYTES BLOB NOT NULL,
                                           PRIMARY KEY (SESSION_PRIMARY_ID, ATTRIBUTE_NAME),
                                           CONSTRAINT SPRING_SESSION_ATTRIBUTES_FK FOREIGN KEY (SESSION_PRIMARY_ID) REFERENCES SPRING_SESSION(PRIMARY_ID) ON DELETE CASCADE
);



CREATE TABLE `oauth_client_details` (
                                        `client_id` varchar(256) NOT NULL,
                                        `resource_ids` varchar(256) DEFAULT NULL,
                                        `password` varchar(256)  NOT NULL,
                                        `client_secret` varchar(256) DEFAULT NULL,
                                        `scope` varchar(256) DEFAULT NULL,
                                        `authorized_grant_types` varchar(256) DEFAULT NULL,
                                        `web_server_redirect_uri` varchar(256) DEFAULT NULL,
                                        `authorities` varchar(256) DEFAULT NULL,
                                        `access_token_validity` int(11) DEFAULT NULL,
                                        `refresh_token_validity` int(11) DEFAULT NULL,
                                        `additional_information` varchar(256) DEFAULT NULL,
                                        `autoapprove` varchar(256) DEFAULT NULL,
                                        PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO oauth_client_details
(client_id, resource_ids, password,client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
VALUES('Futelove', NULL , '$2a$10$oHM29bvWQnR3LOItG2aJAO5P9vDi7p/ZliCbqRvoMwl/qp7gGKDs6', 'client_secret_basic', 'posts:write', 'authorization_code,client_credentials', 'http://localhost:3000/authorized, https://oidcdebugger.com/debug, https://oauth.pstmn.io/v1/callback', 15, 1, NULL, NULL, NULL);