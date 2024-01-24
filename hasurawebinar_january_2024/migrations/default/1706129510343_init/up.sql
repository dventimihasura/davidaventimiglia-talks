SET check_function_bodies = false;
CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;
COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';
CREATE TYPE public.status AS ENUM (
    'new',
    'processing',
    'fulfilled'
);
CREATE TABLE public.product (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    name text NOT NULL,
    price integer NOT NULL,
    CONSTRAINT non_negative_price CHECK ((price > 0))
);
CREATE FUNCTION public.product_search(search text) RETURNS SETOF public.product
    LANGUAGE sql STABLE
    AS $$
  select product.*
  from product
  where
  name ilike ('%' || search || '%')
$$;
CREATE FUNCTION public.product_search_slow(search text, wait real) RETURNS SETOF public.product
    LANGUAGE sql STABLE
    AS $$
  select product.*
  from product, pg_sleep(wait)
  where
  name ilike ('%' || search || '%')
$$;
CREATE FUNCTION public.product_sku(product_row public.product) RETURNS text
    LANGUAGE sql STABLE
    AS $$
  select md5(product_row.name)
$$;
CREATE FUNCTION public.set_current_timestamp_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
  DECLARE
    _new record;
  BEGIN
    _new := NEW;
    _new."updated_at" = NOW();
    RETURN _new;
  END;
$$;
CREATE TABLE public.account (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    name text NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL
);
CREATE TABLE public."order" (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    account_id uuid NOT NULL,
    status public.status,
    region text
);
CREATE TABLE public.order_detail (
    id uuid DEFAULT gen_random_uuid() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    units integer NOT NULL,
    order_id uuid NOT NULL,
    product_id uuid NOT NULL
);
CREATE VIEW public.account_summary AS
 SELECT account.id,
    sum((order_detail.units * product.price)) AS sum
   FROM (((public.account
     JOIN public."order" ON (("order".account_id = account.id)))
     JOIN public.order_detail ON ((order_detail.order_id = "order".id)))
     JOIN public.product ON ((product.id = order_detail.product_id)))
  GROUP BY account.id;
CREATE TABLE public.region (
    value text NOT NULL,
    description text
);
ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public."order"
    ADD CONSTRAINT order_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.product
    ADD CONSTRAINT product_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.region
    ADD CONSTRAINT region_pkey PRIMARY KEY (value);
CREATE INDEX account_name_idx ON public.account USING btree (name);
CREATE INDEX order_account_id_idx ON public."order" USING btree (account_id);
CREATE INDEX order_detail_order_id_idx ON public.order_detail USING btree (order_id);
CREATE INDEX order_detail_product_id_idx ON public.order_detail USING btree (product_id);
CREATE INDEX order_region_idx ON public."order" USING btree (region);
CREATE INDEX order_status_idx ON public."order" USING btree (status);
CREATE TRIGGER set_public_account_updated_at BEFORE UPDATE ON public.account FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_account_updated_at ON public.account IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE TRIGGER set_public_order_detail_updated_at BEFORE UPDATE ON public.order_detail FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_order_detail_updated_at ON public.order_detail IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE TRIGGER set_public_order_updated_at BEFORE UPDATE ON public."order" FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_order_updated_at ON public."order" IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE TRIGGER set_public_product_updated_at BEFORE UPDATE ON public.product FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_product_updated_at ON public.product IS 'trigger to set value of column "updated_at" to current timestamp on row update';
ALTER TABLE ONLY public."order"
    ADD CONSTRAINT order_account_id_fkey FOREIGN KEY (account_id) REFERENCES public.account(id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_order_id_fkey FOREIGN KEY (order_id) REFERENCES public."order"(id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY public.order_detail
    ADD CONSTRAINT order_detail_product_id_fkey FOREIGN KEY (product_id) REFERENCES public.product(id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY public."order"
    ADD CONSTRAINT order_region_fkey FOREIGN KEY (region) REFERENCES public.region(value) ON UPDATE RESTRICT ON DELETE RESTRICT;
