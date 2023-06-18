-- -*- sql-product: mysql; -*-

set sql_mode = 'ANSI_QUOTES';

begin;

insert into region (value, description)
values
  ('NORTHEAST', 'New England'),
  ('MIDWEST', 'Great Lakes'),
  ('SOUTH', 'Dixie'),
  ('PLAINS', 'Great Plains'),
  ('APPALACHIA', 'Pennsylvania and West Virginia'),
  ('MOUNTAIN', 'Rocky Mountains'),
  ('NORTHWEST', 'Rainy'),
  ('WEST', 'California'),
  ('SOUTHWEST', 'Cacti');

commit;
