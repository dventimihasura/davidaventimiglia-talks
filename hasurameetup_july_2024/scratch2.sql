-- -*- sql-product: postgres; -*-

explain analyze

with
  sample
    as not materialized (
      select
	account_id
	from
	  "order"
       limit 10),
  l0
    as not materialized (
      select
	account.id as "account.id",
	account.created_at as "account.created_at",
	account.updated_at as "account.updated_at",
	account.name as "account.name",
	"order".id as "order.id",
	"order".created_at as "order.created_at",
	"order".updated_at as "order.updated_at",
	"order".account_id as "order.account_id",
	"order".status as "order.status",
	"order".region as "order.region",
	order_detail.id as "order_detail.id",
	order_detail.created_at as "order_detail.created_at",
	order_detail.updated_at as "order_detail.updated_at",
	order_detail.order_id as "order_detail.order_id",
	order_detail.product_id as "order_detail.product_id",
	order_detail.units as "order_detail.units",
	product.id as "product.id",
	product.created_at as "product.created_at",
	product.updated_at as "product.updated_at",
	product.name as "product.name",
	product.price as "product.price",
	region.value as "region.value",
	region.description as "region.description"
	from
	  sample
	  join account on account.id = sample.account_id
	  join "order" on "order".account_id = account.id
	  join order_detail on order_detail.order_id = "order".id
	  join product on order_detail.product_id = product.id
	  join region on "order".region = region.value),
  l1
    as not materialized (
      select
	"account.id",
	"account.created_at",
	"account.updated_at",
	"account.name",
	"order.id",
	"order.created_at",
	"order.updated_at",
	"order.account_id",
	"order.status",
	"order.region",
	"order_detail.id",
	"order_detail.created_at",
	"order_detail.updated_at",
	"order_detail.order_id",
	"order_detail.product_id",
	"order_detail.units",
	"product.id",
	"product.created_at",
	"product.updated_at",
	"product.name",
	"product.price",
	(select jsonb_agg(row_to_json(t)) from (select "region.value", "region.description" from l0) t) as "order_detail.regions"
	from
	  l0
       group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21),
  l2
    as not materialized (
      select
	"account.id",
	"account.created_at",
	"account.updated_at",
	"account.name",
	"order.id",
	"order.created_at",
	"order.updated_at",
	"order.account_id",
	"order.status",
	"order.region",
	"order_detail.id",
	"order_detail.created_at",
	"order_detail.updated_at",
	"order_detail.order_id",
	"order_detail.product_id",
	"order_detail.units",
	"order_detail.regions",
	(select jsonb_agg(row_to_json(t)) from (select "product.id", "product.created_at", "product.updated_at", "product.name", "product.price" from l1) t) as "order_detail.products"
	from
	  l1
       group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17),
  l3
    as not materialized (
      select
	"account.id",
	"account.created_at",
	"account.updated_at",
	"account.name",
	"order.id",
	"order.created_at",
	"order.updated_at",
	"order.account_id",
	"order.status",
	"order.region",
	(select jsonb_agg(row_to_json(t)) from (select "order_detail.id", "order_detail.created_at", "order_detail.updated_at", "order_detail.order_id", "order_detail.product_id", "order_detail.units", "order_detail.regions", "order_detail.products" from l2) t) as "order.order_details"
	from
	  l2
       group by 1, 2, 3, 4, 5, 6, 7, 8, 9, 10),
  l4
    as not materialized (
      select
	"account.id",
	"account.created_at",
	"account.updated_at",
	"account.name",
	(select jsonb_agg(row_to_json(t)) from (select "order.id", "order.created_at", "order.updated_at", "order.account_id", "order.status", "order.region", "order.order_details" from l3) t) "account.orders"
	from
	  l3
       group by 1, 2, 3, 4),
  l5
    as not materialized (
      select
	null as "root",
	(select jsonb_agg(row_to_json(t))from (select "account.id", "account.created_at", "account.updated_at", "account.name", "account.orders" from l4) t) "root.accounts"
       group by 1)
select
  jsonb_pretty("root.accounts")
  from
    l5;
