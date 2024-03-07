-- -*- sql-product: postgres; -*-

create extension if not exists postgres_fdw;

create server if not exists catalogdb foreign data wrapper postgres_fdw options (host 'catalogdb', dbname 'postgres', port '5432');

create user mapping if not exists for current_user server catalogdb options (user 'postgres', password 'postgres');

import foreign schema public from server catalogdb into public;
