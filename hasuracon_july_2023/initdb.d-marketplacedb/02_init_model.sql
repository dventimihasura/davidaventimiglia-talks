-- -*- sql-product: postgres; -*-

create extension if not exists pgcrypto;

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

create index on "public".order (account_id);

-- order_detail table

CREATE TABLE "public"."order_detail" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), "units" integer NOT NULL, "order_id" uuid NOT NULL, "product_id" uuid NOT NULL, PRIMARY KEY ("id") , FOREIGN KEY ("order_id") REFERENCES "public"."order"("id") ON UPDATE restrict ON DELETE restrict);
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

create index on "public".order_detail (order_id);

create index on "public".order_detail (product_id);

-- index account(name)

create index if not exists account_name_idx on "public".account (name);

-- status enum

CREATE TYPE status AS ENUM ('new', 'processing', 'fulfilled');

-- add status to order table

alter table "public"."order" add column "status" status null;

create index on "public".order (status);

-- region dictionary table

create table if not exists "public".region (
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

create index on "public".order (region);
