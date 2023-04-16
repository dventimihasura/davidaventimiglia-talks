CREATE TABLE "core"."order" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "created_at" timestamptz NOT NULL DEFAULT now(), "updated_at" timestamptz NOT NULL DEFAULT now(), "account_id" uuid NOT NULL, PRIMARY KEY ("id") , FOREIGN KEY ("account_id") REFERENCES "core"."account"("id") ON UPDATE restrict ON DELETE restrict);
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
CREATE TRIGGER "set_core_order_updated_at"
BEFORE UPDATE ON "core"."order"
FOR EACH ROW
EXECUTE PROCEDURE "core"."set_current_timestamp_updated_at"();
COMMENT ON TRIGGER "set_core_order_updated_at" ON "core"."order" 
IS 'trigger to set value of column "updated_at" to current timestamp on row update';
CREATE EXTENSION IF NOT EXISTS pgcrypto;
