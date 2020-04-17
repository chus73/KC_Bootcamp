### Data Avengers - API Fxcm

Api to extract information from [Forex API](https://github.com/fxcm/RestAPI). More info about the queries in:
 * FXCM API spec [here](https://apiwiki.fxcorporate.com/api/RestAPI/Socket%20REST%20API%20Specs.pdf)
 * Api docs [here](https://fxcm.github.io/rest-api-docs/)

### Configuration

Project was built with:
 * Spring Boot
 * Maven
 * [Lombok](https://projectlombok.org/)
 * SQL Lite
 * Kafka

The **/config/application.properties** file contains the configuration parameters. The following are the most relevant:

 * Common
    * ``server.port:``. Default to 8083
    * ``app.enable.toDatabase:`` Enables storing in database. Default to ``false``
    * ``app.enable.toFile:`` Enables saving in file. Default to ``false``
    * ``app.enable.toKafka:`` Enables send to kafka. Default to ``false``
 * Kafka
    * ``spring.kafka.producer.bootstrap-servers:`` Kafka server location. Example: ``localhost:9092``
    * ``spring.kafka.template.default-topic:`` Kafka producer topic. Example: ``fxcm_topic``
 * FXCM
    * ``api.login.accountId:`` FXCM account ID: ``D261141404``
    * ``api.login.token:`` Token generated from the demo account.
    * ``api.fxcm.get.historical.scheduled.rate:`` Rate time to execute each request: ``150000`` (2.5min)
    * ``api.fxcm.get.historical.instruments:`` Instruments to query separated by ','
    * ``api.fxcm.get.historical.periodId:`` Period to query ``m1,m5,m15,m30,H1,H2,H3,H4,H6,H8,D1,W1,M1``
    * ``api.fxcm.get.historical.elements:`` Elements for each request ``1-10000``

### Kafka configuration

For running kafka in localhost: [Kafka quickstart](https://kafka.apache.org/quickstart). Useful commands for windows:

First download [kafka](https://www.apache.org/dyn/closer.cgi?path=/kafka/2.2.0/kafka_2.12-2.2.0.tgz)

Start Zookeeper

```
$ .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
```

Start kafka server

```
$ .\bin\windows\kafka-server-start.bat .\config\server.properties
```

Create topics

```
$ .\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic fxcm_topic
```

List topics

```
$ .\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

Start consumer console

```
$ .\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic twitter_topic
```

### Output format

```
{
	"instrumentId": "4002",
	"instrument": "XAG/USD",
	"periodId": "m5",
	"candles": [{
		"timestamp": 1555619700,
		"bidOpen": 14.987,
		"bidClose": 14.986,
		"bidHigh": 14.987,
		"bidLow": 14.986,
		"askOpen": 15.029,
		"askClose": 15.028,
		"askHigh": 15.029,
		"askLow": 15.027,
		"tickqty": 25,
		"dateFormatted": "2019-04-18T22:35:00.035Z"
	},...]
}
```
### Output files

  * TXT file under ``/output`` if ``app.enable.toFile`` was enabled
  * SQL lite file under ``/output`` if ``app.enable.toDatabase`` was enabled
  * JSON message to Kafka if ``app.enable.toKafka`` was enabled
  
### Run/Execution

```
$ java -jar dataavengers-fxcm-api.jar
```

Structure for running in cloud or outside Eclipse/IntelliJ. Just copy folders and create a production ready structure like this:

```
.
│   dataavengers-fxcm-api.jar
├───config
│       application.properties
├───logs
│       spring.log
└───output
        fxcm.txt
        fxcm.db
```
### JAR Generation

```
$ ./mvnw clean install package
```

It will generate a **dataavengers-fxcm-api.jar** under ``/target`` folder
