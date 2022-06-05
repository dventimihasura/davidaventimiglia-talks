alter table "public"."product" add constraint "non_negative_price" check (price > 0);
