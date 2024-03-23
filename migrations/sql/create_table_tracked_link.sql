CREATE TABLE IF NOT EXISTS tracked_link
(
    chat_id bigint REFERENCES chat (id) ON DELETE CASCADE,
    link_id bigint REFERENCES link,

    PRIMARY KEY(chat_id, link_id)
);
