create table forbidden_zone
(
    id          serial
        primary key,
    zone_name   varchar(100),
    company_id  bigint
        constraint fk_company
            references company
            on delete set null,
    deactivated boolean default false,
    polygon     geometry
);

alter table forbidden_zone
    owner to postgres;

create index forbidden_zone_polygon_idx
    on forbidden_zone using gist (polygon);

