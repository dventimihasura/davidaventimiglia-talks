alter table "core"."order_detail"
  add constraint "order_detail_order_id_fkey"
  foreign key ("order_id")
  references "core"."order"
  ("id") on update restrict on delete restrict;
