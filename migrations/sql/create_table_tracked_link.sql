CREATE TABLE IF NOT EXISTS tracked_link
(
    chat_id bigint REFERENCES chat ON DELETE CASCADE,
    link_id bigint REFERENCES link,
    last_check timestamp WITH TIME ZONE NOT NULL,

    PRIMARY KEY(chat_id, link_id)
);
