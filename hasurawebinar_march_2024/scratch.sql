-- -*- sql-product: postgres; -*-

EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON)

with
  sample
    as (
      select id account_id from account
    ),
  l4 as (
    select
      jsonb_agg(
	jsonb_build_object(
	  'id', account.id,
	  'created_at', created_at,
	  'updated_at', updated_at,
	  'name', name,
	  'orders', orders))
	filter (where account.id is not null) accounts
      from (
	select
	  "account.id",
	  jsonb_agg(
	    jsonb_build_object(
	      'id', "order".id,
	      'created_at', created_at,
	      'updated_at', updated_at,
	      'account_id', account_id,
	      'status', status,
	      'regions', regions,
	      'order_details', order_details))
	    filter (where "order".id is not null) orders
	  from (
	    select
	      "account.id",
	      "order.id",
	      jsonb_agg(
		jsonb_build_object(
		  'value', region.value,
		  'description', description))
		filter (where region.value is not null) regions,
	      jsonb_agg(
		jsonb_build_object(
		  'id', order_detail.id,
		  'created_at', created_at,
		  'updated_at', created_at,
		  'units', units,
		  'products', products))
		filter (where order_detail.id is not null) order_details
	      from (
		select
		  "account.id",
		  "order.id",
		  "region.value",
		  "order_detail.id",
		  jsonb_agg(
		    jsonb_build_object(
		      'id', product.id,
		      'created_at', created_at,
		      'updated_at', updated_at,
		      'name', name,
		      'price', price))
		    filter (where product.id is not null) products
		  from (
		    select
		      account.id "account.id",
		      "order".id "order.id",
		      region.value "region.value",
		      order_detail.id "order_detail.id",
		      product.id "product.id"
		      from
			sample
			left join account on account_id = account.id
			left join "order" on "order".account_id = account.id
			left join region on "order".region = region.value
			left join order_detail on order_detail.order_id = "order".id
			left join product on order_detail.product_id = product.id) t left join product on product.id = t."product.id"
		 group by 1, 2, 3, 4) t left join order_detail on order_detail.id = t."order_detail.id" left join region on region.value = "region.value"
	     group by 1, 2) t left join "order" on "order".id = t."order.id"
	 group by 1) t left join account on account.id = "account.id")
select pg_column_size(accounts) from l4;
