create table if not exists client
(
    id   bigint not null unique AUTO_INCREMENT,
    name varchar(255),
    primary key (id)
);