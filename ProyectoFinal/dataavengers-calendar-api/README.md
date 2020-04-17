### Data Avengers - API Calendar

Api to extract information from [dailyfx](https://www.dailyfx.com/calendar) calendar

### Configuration

Project was built with:
 * Spring Boot
 * Maven
 * [Lombok](https://projectlombok.org/)
 * SQL Lite
 * Kafka

The **/config/application.properties** file contains the configuration parameters. The following are the most relevant:

 * Common
    * ``server.port:``. Default to 8084
    * ``app.enable.toDatabase:`` Enables storing in database. Default to ``false``
    * ``app.enable.toFile:`` Enables saving in file. Default to ``false``
    * ``app.enable.toKafka:`` Enables send to kafka. Default to ``false``
 * Kafka
    * ``spring.kafka.producer.bootstrap-servers:`` Kafka server location. Example: ``localhost:9092``
    * ``spring.kafka.template.default-topic:`` Kafka producer topic. Example: ``calendar_topic``
 * Calendar
    * ``api.calendar.date.output:`` Output date format
    * ``api.calendar.date.from:`` Calendar start date. Format dd-MM-yyyy
    * ``api.calendar.date.to:``  Calendar end date. Format dd-MM-yyyy

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
$ .\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic calendar_topic
```

List topics

```
$ .\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092
```

Start consumer console

```
$ .\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic calendar_topic
```

### Output format

```
{
	"startDateFormatted": "2019/0428",
	"endDateFormatted": "2019/0504",
	"id": "2019_2019_17",
	"events": [{
		"hour": "08:00",
		"currency": "CHF",
		"event": "CHF Total Sight Deposits CHF (APR 26)",
		"importance": "Low",
		"actual": "576.651b",
		"forecast": "",
		"previous": "576.9b",
		"rawEventDate": "Monday, April 29, 2019",
		"eventDateFormatted": "2019-04-29"
	},...}
```
### Output files

  * TXT file under ``/output`` if ``app.enable.toFile`` was enabled
  * SQL lite file under ``/output`` if ``app.enable.toDatabase`` was enabled
  * JSON message to Kafka if ``app.enable.toKafka`` was enabled

### Run/Execution

```
$ java -jar dataavengers-calendar-api.jar
```

Structure for running in cloud or outside Eclipse/IntelliJ. Just copy folders and create a production ready structure like this:

```
.
│   dataavengers-calendar-api.jar
├───config
│       application.properties
├───logs
│       spring.log
└───output
        calendar.txt
        calendar.db
```
### JAR Generation

```
$ ./mvnw clean install package
```

It will generate a **dataavengers-calendar-api.jar** under ``/target`` folder
