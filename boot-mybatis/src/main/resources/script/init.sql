drop database if exists test;
create database test;

use test;

create table user (
  id       int unsigned primary key not null auto_increment,
  name     varchar(32)              not null,
  password varchar(32)              not null,
  state    tinyint                  not null,
  address  varchar(128)             not null,
  email    varchar(32)              not null
);