alter table "public"."review"
  add constraint "review_city_id_fkey"
  foreign key ("city_id")
  references "public"."city"
  ("id") on update restrict on delete restrict;
