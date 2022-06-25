alter table "core"."order_detail"
  add constraint "order_detail_product_id_fkey"
  foreign key ("product_id")
  references "core"."product"
  ("id") on update restrict on delete restrict;
