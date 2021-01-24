#!/bin/bash

# create kafka topic for file stream source
kafka-topics --create --topic mediamarkt-products-raw --partitions 1 --replication-factor 1 --zookeeper 127.0.0.1:2181

# create kafka topic for rest proxy source
kafka-topics --create --topic bestbuy-products-raw --partitions 1 --replication-factor 1 --zookeeper 127.0.0.1:2181

# create kafka topic for processed
kafka-topics --create --topic products-avro-processed --partitions 1 --replication-factor 1 --zookeeper 127.0.0.1:2181

# configure connect-standalone and connectors
connect-standalone /kafka-config/worker.properties /kafka-config/source-file-stream-mediamarkt-standalone.properties #/kafka-config/sink-cassandra-stream-processed.properties