drop table if exists accounts cascade;
drop table if exists users cascade;
create table users (
                       id bigint primary key,
                       email varchar(255),
                       password varchar(255)
);

create table accounts (
                          id bigint primary key,
                          number varchar(255),
                          balance numeric,
                          currency varchar(255),
                          user_id bigint not null,
                          constraint fk_user foreign key (user_id) references users(id)
);

insert into users(id,email,password) values(1,'1','$2a$12$dyoEluTPU24KZr/LrdEvHuExnGPX4FRM/HWWvWa7t1LAy7EvVZM8W')
