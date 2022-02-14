create table if not exists product
(
id serial,
name varchar(255)
);

create table if not exists trade
(
id serial,
product_id int,
amount int,
date date
);