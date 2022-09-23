create or replace function product_sku(product_row product)
returns text as $$
  select md5(product_row.name)
$$ language sql stable;
