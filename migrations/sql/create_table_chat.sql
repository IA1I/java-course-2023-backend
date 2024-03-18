CREATE TABLE IF NOT EXISTS chat
(
    id bigint GENERATED ALWAYS AS IDENTITY,
    tg_chat_id bigint NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (tg_chat_id)
);
