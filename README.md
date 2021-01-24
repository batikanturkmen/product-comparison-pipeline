The aim of this project is to provide set of services that help the end customer to 
decide which website or retail shop they can use to buy their product by comparing data 
from different providers.


### Setup

Run `docker-compose up` command to run containerized services. You can use `docker-compose down` command to stop services.

#### Requirements

- Docker
- Gradle
- Java 8

### Usage

Kafka Cluster and related components can be monitored and managed by Landoop UI at `http://localhost:3030/`. This UI helps us to see topics, schemas and connectors.

![Kafka Landoop UI](assets/landoop-ui.png)

##### Retrieve All Data

```
Endpoint: /products
Type: GET

http://localhost:9089/products
```

###### Example data for text file.

```
electronics,apple,iphone,1240.8,color,white,memory,128GB
clothing,polo,shirt,10,size,medium,color,purple
clothing,HugoBoss,shirt,12,size,medium,color,purple
electronics,apple,ipad,1678.3,color,red,memory,128GB
clothing,Wrangler,jeans,40,size,medium,color,purple,length,34
automotive,audi,a5,32090,engine,2.0,color,blue,model,sedan
automotive,tesla,models,42090,engine,electric,color,gray,model,sedan
automotive,tesla,modelx,42090,engine,electric,color,gray,model,sedan
automotive,tesla,modelY,43090.6,engine,electric,color,gray,model,sedan
```

###### Example data for REST Proxy.

```
curl -X POST -H "Content-Type: application/vnd.kafka.json.v1+json" \
      --data '  {
  "records": [{
    "value": {
      "category": "automotive",
      "brand": "tesla",
      "product": "modelY",
      "price": 43092.6,
      "additional": {
        "engine": "electric",
        "color": "gray",
        "model": "sedan"
      }
    }
  }]
}' "http://localhost:8082/topics/jsontest2"
  {"offsets":[{"partition":0,"offset":0,"error_code":null,"error":null}],"key_schema_id":null,"value_schema_id":null}
```

```

curl -X POST -H "Content-Type: application/vnd.kafka.json.v1+json" \
      --data '{
  "records": [{
    "value": {
      "category": "clothing",
      "brand": "HugoBoss",
      "product": "shirt",
      "price": 15,
      "additional": {
        "size": "medium",
        "color": "purple"
      }
    }
  }]
}' "http://localhost:8082/topics/jsontest2"
  {"offsets":[{"partition":0,"offset":0,"error_code":null,"error":null}],"key_schema_id":null,"value_schema_id":null}

```

```

curl -X POST -H "Content-Type: application/vnd.kafka.json.v1+json" \
      --data '{
  "records": [{
    "value": {
      "category": "electronics",
      "brand": "ipad",
      "product": "apple",
      "price": 1378.3,
      "additional": {
        "color": "red",
        "memory": "128GB"
      }
    }
  }]
}' "http://localhost:8082/topics/jsontest2"
  {"offsets":[{"partition":0,"offset":0,"error_code":null,"error":null}],"key_schema_id":null,"value_schema_id":null}

```

### Technical Details

#### Pipeline of the project

![Project Pipeline](assets/pipeline-v1.png)

#### Structure of the services

- Stream Services
    - File Stream Processor: Process the data coming from file.
    - Rest Proxy Stream Processor: Process the data comming through rest proxy.
- Backend Services
    - API Gateway: Spring Cloud API Gateway to be able to tracing, routing and load balancing.
    - Naming Service: Service discovery for API Gateway.
    - Product Retrieval Service: Retrieve products with name and category parameters.
#### Integration

The pipeline supports reading and writing various data sources thanks to [Kafka Connectors](https://docs.confluent.io/platform/current/connect/index.html) for importing new product data.
In the project *File Stream Connector* is used to read data from file and *Cassandra Connector* is used to write processed data to the Cassandra as persistent storage.
Moreover, data also can be send by through [Kafka Rest Proxy](https://docs.confluent.io/platform/current/kafka-rest/index.html).

#### Validation and Enrichment

To be able to process the raw data written in the Kafka Topics, [Kafka Streams](https://docs.confluent.io/platform/current/streams/index.html) are used.
Stream processor, clean and validate data with basic checks, enrich it by adding timestamp and source information and convert it to [Apache Avro](https://avro.apache.org/) format.

Another significant enrichment that applied to data is adding ML score of the provider. *(In this project, did not train any model, all scores is given constantly for demonstration purpose.)*

#### Serving

Users can access our data using REST API. [Spring Boot](https://spring.io/projects/spring-boot) is used to handle these requests and database connection.

#### Service Discovery 

![Service Discovery](assets/eureka-service-discovery.png)


#### Ports

| Service 	| Port 	|
|-	|-	|
| **Serving** 	  	|
| Eureka Service Discovery Web 	| 8761 	|
| API Gateway 	| 8765 	|
| Product Retrieval Service 	| 8000 	|
| **Kafka** 	  	|
| Kafka Broker 	| 9092 	|
| Kafka Rest Proxy 	| 8081 	|
| Kafka Schema Registry 	| 8082 	|
| Kafka Connect 	| 8083 	|
| **Data** 	  	|
| Cassandra 	| 9042 	|
| Cassandra Cluster Communication 	| 7000 	|

#### Future works
 - Elasticsearch can be added to retrieve related results.
 - Kubernetes scripts can be developed to run application on the Kubernetes Clusters.
 - More automation can be done to run application with a single command.
 - Testcontainers can be used to test with mock kafka and cassandra instances.
