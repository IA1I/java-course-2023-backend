CREATE TABLE IF NOT EXISTS question (
    link_id bigint REFERENCES link ON DELETE CASCADE,
    comments_count int,
    answers_count int,

    PRIMARY KEY(link_id)
)
