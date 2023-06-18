-- -*- sql-product: postgres; -*-

create extension if not exists pgcrypto;

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

-- product_search function

create or replace function "public".product_search(search text)
  returns setof "public".product as $$
  select product.*
  from "public".product
  where
  name ilike ('%' || search || '%')
$$ language sql stable;

-- product_search_slow function

create or replace function "public".product_search_slow(search text, wait real)
  returns setof "public".product as $$
  select product.*
  from "public".product, pg_sleep(wait)
  where
  name ilike ('%' || search || '%')
$$ language sql stable;

-- non_negative_price constraint

alter table "public"."product" add constraint "non_negative_price" check (price > 0);
