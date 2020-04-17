### Data Avengers - API Twitter

Api to extract information from [Twitter API](https://developer.twitter.com/en/docs/basics/getting-started). More info about the queries in:
 * Search queries [here](https://developer.twitter.com/en/docs/tweets/search/overview/standard)
 * Timeline queries [here](https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html)

### Configuration

Project was built with:
 * Spring Boot
 * Maven
 * [Lombok](https://projectlombok.org/)
 * SQL Lite
 * Kafka

The **/config/application.properties** file contains the configuration parameters. The following are the most relevant:

 * Common
    * ``server.port:``. Default to 8081
    * ``app.enable.toDatabase:`` Enables storing in database. Default to ``false``
    * ``app.enable.toFile:`` Enables saving in file. Default to ``false``
    * ``app.enable.toKafka:`` Enables send to kafka. Default to ``false``
 * Kafka
    * ``spring.kafka.producer.bootstrap-servers:`` Kafka server location. Example: ``localhost:9092``
    * ``spring.kafka.template.default-topic:`` Kafka producer topic. Example: ``twitter_topic``
 * Twitter:
    * ``api.twitter.token:`` API token from Twitter developer account
 * Twitter: Search
    * ``api.twitter.url.search.enabled:`` Enable query search
    * ``api.twitter.url.search.scheduled.rate:`` Rate time to execute each request: ``60000`` (1min)
    * ``api.twitter.url.search.result_type:`` Result type for queries. ``mixed|recent|popular``
    * ``api.twitter.url.search.count:`` Number of results for each request ``1-100``
    * ``api.twitter.url.search.names:`` Names to query separated by ','
    * ``api.twitter.url.search.hashtags:`` Hashtags to query separated by ','
 * Twitter: Timeline
    * ``api.twitter.url.timeline.enabled:`` Enable timeline search
    * ``api.twitter.url.timeline.scheduled.rate:`` Rate time to execute each request: ``60000`` (1min)
    * ``api.twitter.url.timeline.count:`` Number of results for each request ``1-200``
    * ``api.twitter.url.timeline.screen_names:`` Names to query separated by ','

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
$ .\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic twitter_topic
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
	"dateFormatted": "2019-04-18 03:16:38",
	"sourceQuery": "FXstreetNews",
	"hashtags": ["#PBOC", "#USDCNY"],
	"id": 1118684731787313152,
	"id_str": "1118684731787313152",
	"retweet_count": 0,
	"favorite_count": 0,
	"favorited": false,
	"retweeted": false,
	"text": "PBOC sets yuan reference rate at 6.6911 By @godbole17 https://t.co/PuyeDvnb5x #PBOC #USDCNY",
	"created_at": "Thu Apr 18 01:16:38 +0000 2019",
	"lang": "en"
}
```
### Output files

  * TXT file under ``/output`` if ``app.enable.toFile`` was enabled
  * SQL lite file under ``/output`` if ``app.enable.toDatabase`` was enabled
  * JSON message to Kafka if ``app.enable.toKafka`` was enabled

### Run/Execution

```
$ java -jar dataavengers-twitter-api.jar
```

Structure for running in cloud or outside Eclipse/IntelliJ. Just copy folders and create a production ready structure like this:

```
.
│   dataavengers-twitter-api.jar
├───config
│       application.properties
├───logs
│       spring.log
└───output
        tweets.txt
        twitter.db
```
### JAR Generation

```
$ ./mvnw clean install package
```

It will generate a **dataavengers-twitter-api.jar** under ``/target`` folder
