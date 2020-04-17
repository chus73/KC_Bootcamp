### Data Avengers - API News

Api to extract information from [fxstreet](https://www.fxstreet.es/news) webpage
This information is paginated. For this reason, you will see a Result that contains
several hits. Thus, each hit represents a piece of news (only one)

### Configuration

Project was built with:
 * Spring Boot
 * Maven
 * [Lombok](https://projectlombok.org/)
 * SQL Lite
 * Kafka

The **/config/application.properties** file contains the configuration parameters. The following are the most relevant:

 * Common
    * ``server.port:``. Default to 8082
    * ``app.enable.toDatabase:`` Enables storing in database. Default to ``false``
    * ``app.enable.toFile:`` Enables saving in file. Default to ``false``
    * ``app.enable.toKafka:`` Enables send to kafka. Default to ``false``
 * Kafka
    * ``spring.kafka.producer.bootstrap-servers:`` Kafka server location. Example: ``localhost:9092``
    * ``spring.kafka.template.default-topic:`` Kafka producer topic. Example: ``news_topic``
 * News
    * ``api.news.page-items:`` Items for each page
    * ``api.news.date.output:`` Output date format

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
	"dateFormatted": "2019-05-03 18:31:07",
	"Title": "US ISM non-manufacturing index: Another soft reading despite strong NFP - Wells Fargo",
	"Summary": "Analysts at Wells Fargo point out that the expansion in the services sector slowed in April according to the latest ISM non-manufacturing index, which",
	"FullUrl": "https://www.fxstreet.com/news/us-ism-non-manufacturing-index-another-soft-reading-despite-strong-nfp-wells-fargo-201905031631",
	"ImageUrl": "https://editorial.azureedge.net/images/Macroeconomics/Countries/America/UnitedStatesofAmerica/washington-dc-at-dawn-14700037_XtraSmall.jpg",
	"PublicationTime": 1556901067,
	"AuthorName": "Matías Salord",
	"CompanyName": "FXStreet",
	"Category": "News",
	"BusinessId": "4388e855-4d0d-4a84-b996-15f84c8325fe",
	"Tags": ["UnitedStates", "America", "Countries", "Macroeconomics", "Banks", "MarketParticipants"],
	"Article": "Analysts at Wells Fargo point out that the expansion in the services sector slowed in April according to the latest ISM non-manufacturing index, which fell for a second straight month to 55.5, the lowest number since August 2017. Key Quotes: “Any number above 50 still signals expansion, but things have certainly cooled off since the non-manufacturing ISM hit a 13-year high of 60.8 back in September. Taken together with Wednesday’s report that the manufacturing ISM slipped to more than a 2-year low of 52.8, survey data indicate a slowing that is curiously out of step with the solid jobs report & firmer consumer spending reported earlier this week.” “In perhaps the most glaring example of variation between hard and soft data, the employment component of today’s ISM nonmanufacturing showed a 2.2 point decline to 2-year low of 53.7 just 90 minutes after the Labor Department reported job growth for the same month that beat even the highest published forecast.”",
	"objectID": "1425467822",
	"CultureName": "en"
}
```
### Output files

  * TXT file under ``/output`` if ``app.enable.toFile`` was enabled
  * SQL lite file under ``/output`` if ``app.enable.toDatabase`` was enabled
  * JSON message to Kafka if ``app.enable.toKafka`` was enabled

### Run/Execution

```
$ java -jar dataavengers-news-api.jar
```

Structure for running in cloud or outside Eclipse/IntelliJ. Just copy folders and create a production ready structure like this:

```
.
│   dataavengers-news-api.jar
├───config
│       application.properties
├───logs
│       spring.log
└───output
        news.txt
        news.db
```
### JAR Generation

```
$ ./mvnw clean install package
```

It will generate a **dataavengers-news-api.jar** under ``/target`` folder
