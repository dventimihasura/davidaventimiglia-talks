SET check_function_bodies = false;
CREATE DOMAIN public.email AS public.citext
	CONSTRAINT email_check CHECK ((VALUE OPERATOR(public.~) '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$'::public.citext));
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
    id uuid DEFAULT public.gen_random_uuid() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    email public.email NOT NULL,
    name text
);
COMMENT ON TABLE public.account IS 'User Account Table';
CREATE TABLE public.city (
    id uuid DEFAULT public.gen_random_uuid() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    city_id text NOT NULL
);
COMMENT ON TABLE public.city IS 'Fundamental Item in NomadPages';
CREATE VIEW public.random_city AS
 SELECT city.id,
    city.created_at,
    city.updated_at,
    city.city_id
   FROM public.city
  ORDER BY (random())
 LIMIT 1;
CREATE TABLE public.review (
    id uuid DEFAULT public.gen_random_uuid() NOT NULL,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    account_id uuid NOT NULL,
    city_id uuid NOT NULL,
    body text
);
COMMENT ON TABLE public.review IS 'User reviews of cities';
ALTER TABLE ONLY public.account
    ADD CONSTRAINT account_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_city_id_key UNIQUE (city_id);
ALTER TABLE ONLY public.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_pkey PRIMARY KEY (id);
CREATE TRIGGER set_public_account_updated_at BEFORE UPDATE ON public.account FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_account_updated_at ON public.account IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE TRIGGER set_public_city_updated_at BEFORE UPDATE ON public.city FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_city_updated_at ON public.city IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE TRIGGER set_public_review_updated_at BEFORE UPDATE ON public.review FOR EACH ROW EXECUTE FUNCTION public.set_current_timestamp_updated_at();
COMMENT ON TRIGGER set_public_review_updated_at ON public.review IS 'trigger to set value of column "updated_at" to current timestamp on row update';
ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_account_id_fkey FOREIGN KEY (account_id) REFERENCES public.account(id) ON UPDATE RESTRICT ON DELETE RESTRICT;
ALTER TABLE ONLY public.review
    ADD CONSTRAINT review_city_id_fkey FOREIGN KEY (city_id) REFERENCES public.city(id) ON UPDATE RESTRICT ON DELETE RESTRICT;
