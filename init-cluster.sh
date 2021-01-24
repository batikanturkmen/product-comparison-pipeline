#!/bin/bash

# Run kafka worker
docker exec -d kafka-cluster /bin/bash -c "chmod +x /kafka-config/kafka-init.sh; /kafka-config/kafka-init.sh"

# setup kafka cassandra sink connector
curl -X POST \
  localhost:8083/connectors \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "sink-cassandra-stream-processed",
  "config": {
    "connector.class": "com.datamountaineer.streamreactor.connect.cassandra.sink.CassandraSinkConnector",
    "connect.cassandra.key.space": "producttracker",
    "topics": "products-avro-processed",
    "tasks.max": "1",
    "connect.cassandra.kcql": "INSERT INTO products SELECT * FROM products-avro-processed",
    "connect.cassandra.password": "cassandra",
    "connect.cassandra.username": "cassandra",
    "value.converter.schema.registry.url": "http://localhost:8081",
    "connect.cassandra.contact.points": "cassandra",
    "connect.cassandra.port": "9042",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url": "http://localhost:8081"
  }
}'

