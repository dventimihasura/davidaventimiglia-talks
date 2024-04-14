-- -*- sql-product: postgres -*-

-- select login(email, 'password') from basic_auth.users limit 5;

-- insert into basic_auth.users select email, 'password', 'basic_user' from account;

-- select $json$
-- 	 {
-- 	   "type": "HS256",
-- 	   "key": "reallyreallyreallyreallyverysafe"
-- 	   }
-- 	 $json$::jsonb;

update account set email = format($$%s.%s@test.com$$, lower(split_part(name, ' ', 1)), lower(split_part(name, ' ', 2)));

select * from login('john.doe@test.drive', 'password');

select login(email, 'password') from basic_auth.users limit 5;

select distinct email from "order" join account on account.id = account_id limit 5;

with sample as (select distinct email from "order" join account on account.id = account_id limit 5)
    select email, login(email, 'password') from sample;

