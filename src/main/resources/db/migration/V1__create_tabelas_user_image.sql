-- Criar a tabela User
CREATE TABLE IF NOT EXISTS user (
                                    id CHAR(36) PRIMARY KEY,  -- Armazenar UUID como string de 36 caracteres
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    CONSTRAINT UC_user_email UNIQUE (email)  -- Garante que o email seja único
);

-- Criar a tabela Image
CREATE TABLE IF NOT EXISTS image (
                                     id CHAR(36) PRIMARY KEY,  -- Armazenar UUID como string de 36 caracteres
    file_name VARCHAR(255) NOT NULL,
    url VARCHAR(255) NOT NULL,
    user_id CHAR(36),  -- O campo user_id também deve ser CHAR(36) para armazenar UUID
    CONSTRAINT FK_user_image FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);