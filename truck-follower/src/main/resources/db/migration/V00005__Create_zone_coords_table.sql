CREATE TABLE zone_cords_table
(
    id                SERIAL PRIMARY KEY,
    forbidden_zone_id bigint,
    point             point,
    CONSTRAINT fk_forbidden_zone
        FOREIGN KEY (forbidden_zone_id)
            REFERENCES forbidden_zone (id)
            ON DELETE SET NULL
)
