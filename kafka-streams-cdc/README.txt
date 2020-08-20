// start docker-compose.yml

// create tables and enable CDC
cat kafka-streams-cdc/sqlserver/create.sql | docker exec -i sqlserver bash -c '/opt/mssql-tools/bin/sqlcmd -U sa -P $SA_PASSWORD'

// insert data
cat kafka-streams-cdc/sqlserver/insert.sql | docker exec -i sqlserver bash -c '/opt/mssql-tools/bin/sqlcmd -U sa -P $SA_PASSWORD'

// register connector
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @kafka-streams-cdc/sqlserver/register-connector.json

// optional: create topics: server1.dbo.customers if necessary for the stream
docker-compose exec kafka kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1o --topic server1.dbo.customers

// start KafkaApplication
....

// browser insert customer
http://localhost:8080/customers/insert?count=1
http://localhost:8080/send-messages?count=1

//
cd  kafka-streams-producer
./mvnw quarkus:dev

cd  kafka-streams-aggregator
./mvnw quarkus:dev

// consumer in console, Development/Kafka/kafka-2.4.0-src/bin
sh kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --property print.key=true --topic server1.dbo.customers

// performance on myTopic
http://localhost:8080/send-messages?count=1000000


//docker-compose exec kafka "/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --from-beginning --property print.key=true --topic server1.dbo.customers"

// kafka Development/Kafka/kafka-2.4.0-src/bin
sh kafka-topics.sh --bootstrap-server localhost:9092 --list
