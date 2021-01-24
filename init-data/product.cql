DROP KEYSPACE IF exists producttracker;

CREATE KEYSPACE producttracker WITH replication = {
  'class':'SimpleStrategy',
  'replication_factor':1
};

USE producttracker;

CREATE TABLE products (
id text,
created_at bigint,
source text,
brand text,
product text,
category text,
price float,
recommendation_score float,
additional text,
PRIMARY KEY ((category, product, source), recommendation_score))
WITH CLUSTERING ORDER BY (recommendation_score ASC);