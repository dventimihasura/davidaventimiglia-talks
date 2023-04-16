CREATE TABLE "core"."order_detail" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), "units" integer NOT NULL, "product_id" UUID NOT NULL, "order_id" uuid NOT NULL, PRIMARY KEY ("id") );
CREATE OR REPLACE FUNCTION "core"."set_current_timestamp_updated_at"()
RETURNS TRIGGER AS $$
DECLARE
  _new record;
BEGIN
  _new := NEW;
  _new."updated_at" = NOW();
  RETURN _new;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER "set_core_order_detail_updated_at"
BEFORE UPDATE ON "core"."order_detail"
FOR EACH ROW
EXECUTE PROCEDURE "core"."set_current_timestamp_updated_at"();
COMMENT ON TRIGGER "set_core_order_detail_updated_at" ON "core"."order_detail" 
IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE EXTENSION IF NOT EXISTS pgcrypto;
