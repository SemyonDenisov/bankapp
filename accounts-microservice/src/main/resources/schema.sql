drop table if exists accounts cascade;
drop table if exists users cascade;
create table users
(
    id       bigserial primary key,
    email    varchar(255),
    password varchar(255),
    username varchar(255),
    birthday date
);

create table accounts
(
    id       bigserial primary key,
    number   varchar(255),
    balance  numeric,
    currency varchar(255),
    user_id  bigint not null,
    constraint fk_user foreign key (user_id) references users (id)
);

insert into users(id, email, password,username,birthday) values (0, '1', '$2a$12$dyoEluTPU24KZr/LrdEvHuExnGPX4FRM/HWWvWa7t1LAy7EvVZM8W','senja',DATE '2005-08-30');
insert into accounts(id, number, balance, currency, user_id) values (0, '777', 70.0, 'RUB', 0);
insert into users(id, email, password,username,birthday) values (1, '2', '$2a$12$dyoEluTPU24KZr/LrdEvHuExnGPX4FRM/HWWvWa7t1LAy7EvVZM8W','nesenja',DATE '2005-08-30');
insert into accounts(id, number, balance, currency, user_id) values (1, '777', 170.0, 'RUB', 1);