CREATE TABLE IF NOT EXISTS chat
(
    chat_id bigint GENERATED ALWAYS AS IDENTITY,
    tg_chat_id bigint NOT NULL,

    PRIMARY KEY (chat_id),
    UNIQUE (chat_id)
);
