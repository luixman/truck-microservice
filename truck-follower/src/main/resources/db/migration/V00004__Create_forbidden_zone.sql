CREATE TABLE forbidden_zone
(
    id          SERIAL PRIMARY KEY,
    zone_name   varchar(100),
    company_id  bigint,
    deactivated bool,

    constraint fk_company
        FOREIGN KEY (company_id)
            REFERENCES company (id)
            ON DELETE SET NULL
)



