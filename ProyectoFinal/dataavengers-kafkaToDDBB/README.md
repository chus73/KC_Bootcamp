## Dara Avengers - Spark TO BBDD process

Process to read information from kafka topics

Project was built with:

 * Spark 2.3.2 , Streaming and SQL
 * Kafka 0.10.0
 * Maven
 * Cats funtional library
 * MongoDB batch API

## Configuration
### File config
The **connections.conf** file contains the configuration parameters. The following are the most relevant:

 * fxcmConfig
    * Kafka config:
        * bootstrapServers: kafka server url, example: "localhost9092"
        * topics: array topic ti suscribe, example: ["fxcm_topic"]
        * maxOffSetsPerBatch:200
        * startingOffsets = offset to read from kafka first time, example: "latest"
    * MongoDb config:
        * uri: path to databse, user and pass , example:"mongodb://url"
        * collection : "calendar"
        * checkpointPath : checkpoint path , example: "/Data/calendar/checkpoint/persistAll"
        * database : example: "Spark"
        * ammountPerBatch = 200
 * calendarConfig
     * Kafka config:
         * bootstrapServers: kafka server url, example: "localhost9092"
         * topics: array topic ti suscribe, example: ["fxcm_topic"]
         * maxOffSetsPerBatch:200
         * startingOffsets = offset to read from kafka first time, example: "latest"
     * MongoDb config:
         * uri: path to databse, user and pass , example:"mongodb://url"
         * collection : "calendar"
         * checkpointPath : checkpoint path , example: "/Data/calendar/checkpoint/persistAll"
         * database : example: "Spark"
         * ammountPerBatch = 200
 * newsConfig
     * Kafka config:
         * bootstrapServers: kafka server url, example: "localhost9092"
         * topics: array topic ti suscribe, example: ["fxcm_topic"]
         * maxOffSetsPerBatch:200
         * startingOffsets = offset to read from kafka first time, example: "latest"
     * MongoDb config:
         * uri: path to databse, user and pass , example:"mongodb://url"
         * collection : "calendar"
         * checkpointPath : checkpoint path , example: "/Data/calendar/checkpoint/persistAll"
         * database : example: "Spark"
         * ammountPerBatch = 200
 * twitterConfig
     * Kafka config:
         * bootstrapServers: kafka server url, example: "localhost9092"
         * topics: array topic ti suscribe, example: ["fxcm_topic"]
         * maxOffSetsPerBatch:200
         * startingOffsets = offset to read from kafka first time, example: "latest"
     * MongoDb config:
         * uri: path to databse, user and pass , example:"mongodb://url"
         * collection : "calendar"
         * checkpointPath : checkpoint path , example: "/Data/calendar/checkpoint/persistAll"
         * database : example: "Spark"
         * ammountPerBatch = 200
 
### Argument config
 
The program accepts two argument:

* Configuration file path: Spark will try to find the file on hadoop and local file system.
* Configuration mode
    * "streaming": streaming to database until the program finishes by the user. By default.
    * "show": streaming showing result in standard output.
    * "batch": bach process to database, read all rows from kafka and finish.        
         
### Run/Execution with Spark Submit and jar file

On windows the user must have configured winutils.exe for running hadoop:

https://wiki.apache.org/hadoop/WindowsProblems

To execute the spark process must to execute the following commands:

* Streaming mode by default

spark-submit --master spark://master_host_ip:7077 uber-dataavengers-spark-KafkaToBBDD-0.1-SNAPSHOT.jar /usr/SparkApp/connections.conf

* Streaming mode

spark-submit --master spark://master_host_ip:7077 uber-dataavengers-spark-KafkaToBBDD-0.1-SNAPSHOT.jar /usr/SparkApp/connections.conf streaming

* Batch mode

spark-submit --master spark://master_host_ip:7077 uber-dataavengers-spark-KafkaToBBDD-0.1-SNAPSHOT.jar /usr/SparkApp/connections.conf batch

* Show mode for testing

spark-submit --master spark://master_host_ip:7077 uber-dataavengers-spark-KafkaToBBDD-0.1-SNAPSHOT.jar /usr/SparkApp/connections.conf show

### Run/Execution on IntelliJ 


For run the program using IntelliJ you must create a configuration with the following parameters:

* Main class :com.kc.ark.AppFlat
* Vm Options: -Dspark.master=local[2]
* Program arguments: ./connections.conf show
* Check de checkBox "Include dependencies with provided scope"

         
### JAR Generation

```
$ ./mvnw clean install package
```

It will generate a **uber-dataavengers-spark-KafkaToBBDD-0.1-SNAPSHOT.jar** under ``/target`` folder 