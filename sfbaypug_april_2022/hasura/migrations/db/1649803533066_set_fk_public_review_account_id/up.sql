alter table "public"."review"
  add constraint "review_account_id_fkey"
  foreign key ("account_id")
  references "public"."account"
  ("id") on update restrict on delete restrict;
