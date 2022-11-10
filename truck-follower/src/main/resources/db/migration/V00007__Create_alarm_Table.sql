CREATE TABLE alarm(
    id serial PRIMARY KEY ,
    truck_id bigint,
    forbidden_zone_id bigint,
    time date,

    zone_leave bool default false,

    leave_time date,
    archive bool default false


)