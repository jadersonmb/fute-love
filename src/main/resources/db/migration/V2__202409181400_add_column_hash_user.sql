-- Altera a coluna hash_code para o tipo int
alter table user
    add column if not exists
    hash_code int not null default 0;
