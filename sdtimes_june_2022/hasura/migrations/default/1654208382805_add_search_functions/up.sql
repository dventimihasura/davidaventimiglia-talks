create or replace function product_search(search text)
  returns setof product as $$
  select *
  from product
  where
  name ilike ('%' || search || '%')
$$ language sql stable;

create extension if not exists pg_trgm;

create index if not exists product_gin_idx on product
using gin ((name) gin_trgm_ops);

create or replace function product_fuzzy_search(search text)
  returns setof product as $$
  select *
  from product
  where
  search <% (name)
  order by
  similarity(search, (name)) desc
  limit 5;
$$ language sql stable;
