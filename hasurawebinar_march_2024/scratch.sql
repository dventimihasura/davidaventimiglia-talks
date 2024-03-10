-- -*- sql-product: postgres; -*-

with
  sample
    as (
      select
	account_id
	from
	  "order"
       limit 1)
select
  distinct region.*
  from
    sample
    join account on account_id = account.id
    join "order" on "order".account_id = account.id
    join order_detail on order_detail.order_id = "order".id
    join product on order_detail.product_id = product.id
    join region on "order".region = region.value;

with
  sample
    as (
      select
	account_id
	from
	  "order"
       limit 1),
  l0
    as (
      select
	account.id "account.id",
	account.created_at "account.created_at",
	account.updated_at "account.updated_at",
	account.name "account.name",
	"order".id "order.id",
	"order".created_at "order.created_at",
	"order".updated_at "order.updated_at",
	"order".status "order.status",
	"order".region "order.region",
	order_detail.id "order_detail.id",
	order_detail.created_at "order_detail.created_at",
	order_detail.updated_at "order_detail.updated_at",
	order_detail.order_id "order_detail.order_id",
	order_detail.units "order_detail.units",
	product.id "product.id",
	product.created_at "product.created_at",
	product.updated_at "product.updated_at",
	product.name "product.name",
	product.price "product.price",
	region.value "region.value",
	region.description "region.description"
	from
	  sample
	  join account on account_id = account.id
	  join "order" on "order".account_id = account.id
	  join order_detail on order_detail.order_id = "order".id
	  join product on order_detail.product_id = product.id
	  join region on "order".region = region.value)
select
  jsonb_pretty(to_jsonb(row(t))->'f1') accounts
  from (
    select
      "account.id" id,
      "account.created_at" created_at,
      "account.updated_at" updated_at,
      "account.name" name,
      jsonb_agg(to_jsonb(row(t))->'f1') orders
      from (
	select
	  "order.id" id,
	  "order.created_at" created_at,
	  "order.updated_at" updated_at,
	  "order.status" status,
	  "order.region" region,
	  jsonb_agg(to_jsonb(row(t))->'f1') order_details
	  from (
	    select
	      "order_detail.id" id,
	      "order_detail.created_at" created_at,
	      "order_detail.updated_at" updated_at,
	      "order_detail.order_id" order_id,
	      "order_detail.units" units
	      from
		l0) t
	       join l0 on l0."order_detail.id" = t.id
	 group by 1, 2, 3, 4, 5) t
	   join l0 on l0."order.id" = t.id
     group by 1, 2, 3, 4) t;

with
  sample
    as (
      select 'd3ad48c0-97df-4b72-9e4f-93dd745ff8b1'::uuid account_id
    ),
  l0
    as (
      select
	account.id "account.id",
	account.created_at "account.created_at",
	account.updated_at "account.updated_at",
	account.name "account.name",
	"order".id "order.id",
	"order".created_at "order.created_at",
	"order".updated_at "order.updated_at",
	"order".status "order.status",
	"order".region "order.region",
	order_detail.id "order_detail.id",
	order_detail.created_at "order_detail.created_at",
	order_detail.updated_at "order_detail.updated_at",
	order_detail.order_id "order_detail.order_id",
	order_detail.units "order_detail.units",
	product.id "product.id",
	product.created_at "product.created_at",
	product.updated_at "product.updated_at",
	product.name "product.name",
	product.price "product.price",
	region.value "region.value",
	region.description "region.description"
	from
	  sample
	  join account on account_id = account.id
	  join "order" on "order".account_id = account.id
	  join order_detail on order_detail.order_id = "order".id
	  join product on order_detail.product_id = product.id
	  join region on "order".region = region.value)
select
  jsonb_pretty(jsonb_agg(to_jsonb(row(t))->'f1'))
  from
    l0
    join (
      select
	distinct
	"account.id" id,
	"account.created_at" created_at,
	"account.updated_at" updated_at,
	"account.name" name,
	jsonb_agg(to_jsonb(row(t))->'f1') orders
	from
	  l0
	  join (
	    select
	      distinct
	      "order.id" id,
	      "order.created_at" created_at,
	      "order.updated_at" updated_at,
	      "order.status" status,
	      "order.region" region,
	      jsonb_agg(to_jsonb(row(u))->'f1') regions,
	      jsonb_agg(to_jsonb(row(t))->'f1') order_details
	      from l0
		   join (
		     select
		       distinct
		       "order_detail.id" id,
		       "order_detail.created_at" created_at,
		       "order_detail.updated_at" updated_at,
		       "order_detail.units" units,
		       jsonb_agg(to_jsonb(row(t))->'f1') products
		       from l0
			    join (
			      select
				"product.id" id,
				"product.created_at" created_at,
				"product.updated_at" updated_at,
				"product.name" name,
				"product.price" price
				from l0
			    ) t on l0."product.id" = t.id
		      group by 1, 2, 3, 4
		   ) t on l0."order_detail.id" = t.id
		   join (
		     select
		       distinct
		       "region.value" value,
		       "region.description" description
		       from l0
		   ) u on l0."order.region" = u.value
	     group by 1, 2, 3, 4, 5
	  ) t on l0."order.id" = t.id
       group by 1, 2, 3, 4
    ) t on l0."account.id" = t.id;
