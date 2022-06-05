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
CREATE EXTENSION IF NOT EXISTS pgcrypto;
