CREATE TABLE IF NOT EXISTS link
(
    link_id bigint GENERATED ALWAYS AS IDENTITY,
    uri text UNIQUE NOT NULL,
    updated_at timestamp WITH TIME ZONE NOT NULL,

    PRIMARY KEY(link_id),
    UNIQUE(uri)
);
