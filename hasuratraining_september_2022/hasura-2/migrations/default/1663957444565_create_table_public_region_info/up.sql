CREATE TABLE "public"."region_info" ("id" uuid NOT NULL DEFAULT gen_random_uuid(), "name" text NOT NULL, "sales_director" text NOT NULL, PRIMARY KEY ("id") );
CREATE EXTENSION IF NOT EXISTS pgcrypto;
