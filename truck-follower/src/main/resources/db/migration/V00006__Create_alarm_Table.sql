create table alarm
(
    id                 serial
        primary key,
    truck_id           bigint,
    forbidden_zone_id  bigint,
    message_time       timestamp with time zone,
    zone_leave         boolean default false,
    leave_time         timestamp with time zone,
    message_time_wrong boolean default false,
    point_entry        bytea,
    point_exit         bytea
);

alter table alarm
    owner to postgres;
