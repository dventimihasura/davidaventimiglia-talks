-- -*- sql-product: postgres; -*-

create schema if not exists public;

CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- account table

CREATE TABLE "public"."account" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "name" text NOT NULL, "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), PRIMARY KEY ("id") );
CREATE OR REPLACE FUNCTION "public"."set_current_timestamp_updated_at"()
  RETURNS TRIGGER AS $$
  DECLARE
    _new record;
  BEGIN
    _new := NEW;
    _new."updated_at" = NOW();
    RETURN _new;
  END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER "set_public_account_updated_at"
  BEFORE UPDATE ON "public"."account"
  FOR EACH ROW
  EXECUTE PROCEDURE "public"."set_current_timestamp_updated_at"();
COMMENT ON TRIGGER "set_public_account_updated_at" ON "public"."account" 
  IS 'trigger to set value of column "updated_at" to current timestamp on row update';

-- product table

CREATE TABLE "public"."product" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), "name" text NOT NULL, "price" integer NOT NULL, PRIMARY KEY ("id") );
CREATE OR REPLACE FUNCTION "public"."set_current_timestamp_updated_at"()
  RETURNS TRIGGER AS $$
  DECLARE
    _new record;
  BEGIN
    _new := NEW;
    _new."updated_at" = NOW();
    RETURN _new;
  END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER "set_public_product_updated_at"
  BEFORE UPDATE ON "public"."product"
  FOR EACH ROW
  EXECUTE PROCEDURE "public"."set_current_timestamp_updated_at"();
COMMENT ON TRIGGER "set_public_product_updated_at" ON "public"."product" 
  IS 'trigger to set value of column "updated_at" to current timestamp on row update';

-- order table

CREATE TABLE "public"."order" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), "account_id" uuid NOT NULL, PRIMARY KEY ("id") , FOREIGN KEY ("account_id") REFERENCES "public"."account"("id") ON UPDATE restrict ON DELETE restrict);
CREATE OR REPLACE FUNCTION "public"."set_current_timestamp_updated_at"()
  RETURNS TRIGGER AS $$
  DECLARE
    _new record;
  BEGIN
    _new := NEW;
    _new."updated_at" = NOW();
    RETURN _new;
  END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER "set_public_order_updated_at"
  BEFORE UPDATE ON "public"."order"
  FOR EACH ROW
  EXECUTE PROCEDURE "public"."set_current_timestamp_updated_at"();
COMMENT ON TRIGGER "set_public_order_updated_at" ON "public"."order" 
  IS 'trigger to set value of column "updated_at" to current timestamp on row update';

create index on "order" (account_id);

-- order_detail table

CREATE TABLE "public"."order_detail" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), "units" integer NOT NULL, "order_id" uuid NOT NULL, "product_id" uuid NOT NULL, PRIMARY KEY ("id") , FOREIGN KEY ("order_id") REFERENCES "public"."order"("id") ON UPDATE restrict ON DELETE restrict, FOREIGN KEY ("product_id") REFERENCES "public"."product"("id") ON UPDATE restrict ON DELETE restrict);
CREATE OR REPLACE FUNCTION "public"."set_current_timestamp_updated_at"()
  RETURNS TRIGGER AS $$
  DECLARE
    _new record;
  BEGIN
    _new := NEW;
    _new."updated_at" = NOW();
    RETURN _new;
  END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER "set_public_order_detail_updated_at"
  BEFORE UPDATE ON "public"."order_detail"
  FOR EACH ROW
  EXECUTE PROCEDURE "public"."set_current_timestamp_updated_at"();
COMMENT ON TRIGGER "set_public_order_detail_updated_at" ON "public"."order_detail" 
  IS 'trigger to set value of column "updated_at" to current timestamp on row update';

create index on order_detail (order_id);

create index on order_detail (product_id);

-- product_search function

create or replace function product_search(search text)
  returns setof product as $$
  select product.*
  from product
  where
  name ilike ('%' || search || '%')
$$ language sql stable;

-- product_search_slow function

create or replace function product_search_slow(search text, wait real)
  returns setof product as $$
  select product.*
  from product, pg_sleep(wait)
  where
  name ilike ('%' || search || '%')
$$ language sql stable;

-- non_negative_price constraint

alter table "public"."product" add constraint "non_negative_price" check (price > 0);

-- index account(name)

create index if not exists account_name_idx on account (name);

-- status enum

CREATE TYPE status AS ENUM ('new', 'processing', 'fulfilled');

-- add status to order table

alter table "public"."order" add column "status" status null;

create index on "order" (status);

-- region dictionary table

create table if not exists region (
  value text primary key,
  description text);

-- add region to order

alter table "public"."order" add column "region" Text
 null;

alter table "public"."order"
  add constraint "order_region_fkey"
  foreign key ("region")
  references "public"."region"
  ("value") on update restrict on delete restrict;

create index on "order" (region);

-- account summary view

create or replace view account_summary as
select
  account.id,
  sum(units*price)
  from account
       join "order" on "order".account_id = account.id
       join order_detail on order_detail.order_id = "order".id
       join product on product.id = order_detail.product_id
 group by account.id;

-- sku function

create or replace function product_sku(product_row product)
returns text as $$
  select md5(product_row.name)
$$ language sql stable;

----	      

create or replace function url_encode (data bytea)
  returns text
  language sql as $$
  select translate(encode(data, 'base64'), e'+/=\n', '-_');
$$ immutable;

create or replace function url_decode(data text)
  returns bytea
  language sql as $$
  with t as (select translate(data, '-_', '+/') as trans),
  rem as (select length(t.trans) % 4 as remainder from t)
  select decode(
    t.trans ||
    case when rem.remainder > 0
    then repeat('=', (4 - rem.remainder))
    else '' end,
    'base64') from t, rem;
$$ immutable;

create or replace function algorithm_sign (signables text, secret text, algorithm text)
  returns text language sql as $$
  with
  alg as (
    select case
	   when algorithm = 'HS256' then 'sha256'
	   when algorithm = 'HS384' then 'sha384'
	   when algorithm = 'HS512' then 'sha512'
	   else '' end as id)
  select public.url_encode(public.hmac(signables, secret, alg.id)) from alg;
$$ immutable;

create or replace function sign (payload json, secret text, algorithm text default 'HS256')
  returns text
  language sql as $$
  with
  header as (
    select public.url_encode(convert_to('{"alg":"' || algorithm || '","typ":"jwt"}', 'utf8')) as data
  ),
  payload as (
    select public.url_encode(convert_to(payload::text, 'utf8')) as data
  ),
  signables as (
    select header.data || '.' || payload.data as data from header, payload
  )
  select
  signables.data || '.' ||
  public.algorithm_sign(signables.data, secret, algorithm) from signables;
$$ immutable;

create or replace function verify (token text, secret text, algorithm text default 'HS256')
  returns table(header json, payload json, valid boolean)
  language sql as $$
  select
  convert_from(public.url_decode(r[1]), 'utf8')::json as header,
  convert_from(public.url_decode(r[2]), 'utf8')::json as payload,
  r[3] = public.algorithm_sign(r[1] || '.' || r[2], secret, algorithm) as valid
  from regexp_split_to_array(token, '\.') r;
$$ immutable;

----

create table if not exists credentials (
  account_id uuid NOT NULL references account (id),
  email    text primary key check ( email ~* '^.+@.+\..+$' ),
  pass     text not null check (length(pass) < 512),
  role     name not null check (length(role) < 512)
);

create or replace function encrypt_pass()
  returns trigger as $$
  begin
    if tg_op = 'INSERT' or new.pass <> old.pass then
      new.pass = crypt(new.pass, gen_salt('bf'));
    end if;
    return new;
  end
$$ language plpgsql;

create trigger encrypt_pass
  before insert or update on credentials
  for each row
  execute procedure encrypt_pass();

create or replace function user_role (email text, pass text)
  returns name
  language plpgsql
as $$
  begin
    return (
      select role from credentials
       where credentials.email = user_role.email
	 and credentials.pass = crypt(user_role.pass, credentials.pass)
    );
  end;
$$;

create type jwt_token as (
  token text
);

create function jwt_test () returns public.jwt_token as $$
  select public.sign(
    row_to_json(r), 'reallyreallyreallyreallyverysafe'
  ) as token
  from (
    select
      'my_role'::text as role,
      extract(epoch from now())::integer + 300 as exp
  ) r;
$$ language sql;

create or replace function login (email text, pass text)
  returns jwt_token as $$
  declare
    _role name;
    result jwt_token;
  begin
    select user_role(email, pass) into _role;
    if _role is null then
      raise invalid_password using message = 'invalid user or password';
    end if;
    select sign(
      row_to_json(r), 'reallyreallyreallyreallyverysafe'
    ) as token
      from (
	select _role as role,
	       format($json$
		      {
			"x-hasura-default-role": "%s",
			"x-hasura-allowed-roles": ["%s"],
			"x-hasura-user-id": "%s"
			}		      
			$json$, _role, _role, email)::jsonb as "https://hasura.io/jwt/claims",
	       login.email as email,
               extract(epoch from now())::integer + 60*60 as exp
      ) r
      into result;
    return result;
  end;
$$ language plpgsql security definer;

create view session as
  select null::text email, null::jwt_token token where false;

create or replace function get_session (email text, pass text)
  returns setof session
  language sql
  stable
as $sql$
  select email, login(email, pass)
  $sql$;

