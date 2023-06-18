-- -*- sql-product: mysql; -*-

set sql_mode = 'ANSI_QUOTES';

set foreign_key_checks = 0;

drop table if exists "region";

-- region dictionary table

create table if not exists region (
  value varchar(255) primary key,
  description varchar(255));


