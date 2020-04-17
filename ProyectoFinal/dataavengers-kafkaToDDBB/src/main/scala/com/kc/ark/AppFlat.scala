package com.kc.ark

import cats.syntax.either._
import com.kc.ark.config.AppConfig
import com.kc.ark.model.{FcxmInput, TwitterInput}
import com.kc.ark.process.{Modes, Transformation}
import com.kc.ark.utils.{ConfigUtils, MongoUtils, MyKafkaUtils}
import main.scala.com.kc.ark.model.{CalendarInput, NewsInput}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.sql.streaming.StreamingQuery
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.slf4j.LoggerFactory

object AppFlat {

  private val logger = LoggerFactory.getLogger(AppFlat.getClass)
  import com.kc.ark.config.ArgumentsParser.argumentsTags._
  import com.kc.ark.config.ArgumentsParser.parseArguments

  def main(args: Array[String]): Unit = {
    implicit val sparkSession: SparkSession = SparkSession
      .builder()
      .appName("DataavengersKafkaToDDBB")
      .getOrCreate()

    val r = for {
      argumentsParsed <- parseArguments(args) //parse the arguments
      config <- ConfigUtils.getConfig(argumentsParsed(configPathTag)) //read the configuration according the arguments
      result <- launchSelected(config, argumentsParsed(modeTag))
    } yield result

    r.right.foreach(_ => logger.info("exited correctly"))
    r.left.foreach(logger.error("error in the execution of the Application",_))
  }

  def launchSelected(config:AppConfig, mode:String)(implicit sparkSession: SparkSession): Either[Throwable,Unit] = {
    for {
      processResult <- Either.catchNonFatal(launchMode(new Transformation,mode,config))
    } yield processResult
  }

  /**
    * Selects the mode to launch,
    * 'streaming' mode, creates the streame that parse, transform and write to cosmos.
    * 'last' mode, read, parse, filter only the last event for each id and write into cosmos.
    * 'batch' mode, is the same as streaming but in a finite batch until no messages are found in kafka.
    * 'show' mode, stream that reads, parse, and transform and shows in console the result
    * 'count' mode, batch that counts the number of messages in kafka.
    *
    * @param process the transformation to apply if needed
    * @param mode the mode to run
    * @param config the configuration of the application
    * @param sparkSession the spark session to use
    * @return the error if something went wrong or unit if all went ok
    */
  def launchMode(process:Transformation, mode:String,
                 config:AppConfig)(implicit sparkSession: SparkSession): Either[Throwable, Unit] = {
    import process._
    import sparkSession.implicits._

    mode match {
      case Modes.StreamingMode =>
        Either.catchNonFatal {
          val ssc = new StreamingContext(sparkSession.sparkContext, Seconds(1))

          val fxcmKafkaStream = MyKafkaUtils.readAsStreamDStream(config.fxcmConfig.kafkaConfig,ssc)
          val twitterKafkaStream = MyKafkaUtils.readAsStreamDStream(config.twitterConfig.kafkaConfig,ssc)
          val calendarKafkaStream = MyKafkaUtils.readAsStreamDStream(config.calendarConfig.kafkaConfig,ssc)
          val newsKafkaStream = MyKafkaUtils.readAsStreamDStream(config.newsConfig.kafkaConfig,ssc)

          fxcmKafkaStream.foreachRDD({ rdd =>
            val df = sparkSession
              .createDataFrame(rdd)
              .toDF("id","value")
              .drop("id")
              .transform(processFcxmMessage[FcxmInput])

            MongoUtils.writeAsBatchMongo2(config.fxcmConfig.mongoConfig)(df)
          })

          twitterKafkaStream.foreachRDD({ rdd =>
            val df = sparkSession
              .createDataFrame(rdd)
              .toDF("id","value")
              .drop("id")
              .transform(processTwitterMessage[TwitterInput])

            MongoUtils.writeAsBatchMongo2(config.twitterConfig.mongoConfig)(df)
          })

          calendarKafkaStream.foreachRDD({ rdd =>
            val df = sparkSession
              .createDataFrame(rdd)
              .toDF("id","value")
              .drop("id")
              .transform(processCalendarMessage[CalendarInput])

            MongoUtils.writeAsBatchMongo2(config.calendarConfig.mongoConfig)(df)
          })

          newsKafkaStream.foreachRDD({ rdd =>
            val df = sparkSession
              .createDataFrame(rdd)
              .toDF("id","value")
              .drop("id")
              .transform(processNewsMessage[NewsInput])

            MongoUtils.writeAsBatchMongo2(config.newsConfig.mongoConfig)(df)
          })


          logger.info("Streaming mode configured and working")
          ssc.start()
          ssc.awaitTermination()
        }
      case Modes.BatchMode =>
        Either.catchNonFatal {
          lazy val fcxmTransformation = MyKafkaUtils.extractValueFromKafkaMessage andThen processFcxmMessage[FcxmInput]
          lazy val twitterTransformation = MyKafkaUtils.extractValueFromKafkaMessage andThen processTwitterMessage[TwitterInput]
          val fxcmKafkaBatch = MyKafkaUtils.readAsBatch(config.fxcmConfig.kafkaConfig)
          val twitterKafkaBatch = MyKafkaUtils.readAsBatch(config.twitterConfig.kafkaConfig)

          val batchFxcm = fxcmKafkaBatch
            .transform(
              fcxmTransformation)

          val batchTwitter = twitterKafkaBatch
            .transform(
              twitterTransformation)

          logger.info("Batch mode configured and working")
          MongoUtils.writeAsBatchMongo(config.fxcmConfig.mongoConfig)(batchFxcm)
          MongoUtils.writeAsBatchMongo(config.twitterConfig.mongoConfig)(batchTwitter)
        }
      case Modes.ShowMode =>
        Either.catchNonFatal {
          lazy val fcxmTransformation = MyKafkaUtils.extractValueFromKafkaMessage andThen processFcxmMessage[FcxmInput]
          lazy val twitterTransformation = MyKafkaUtils.extractValueFromKafkaMessage andThen processTwitterMessage[TwitterInput]
          lazy val calendarTransformation = MyKafkaUtils.extractValueFromKafkaMessage andThen processCalendarMessage[CalendarInput]
          lazy val newsTransformation = MyKafkaUtils.extractValueFromKafkaMessage andThen processNewsMessage[NewsInput]


          val fxcmKafkaStream = MyKafkaUtils.readAsStream(config.fxcmConfig.kafkaConfig)
          val twitterKafkaStream = MyKafkaUtils.readAsStream(config.twitterConfig.kafkaConfig)
          val calendarKafkaStream = MyKafkaUtils.readAsStream(config.calendarConfig.kafkaConfig)
          val newsKafkaStream = MyKafkaUtils.readAsStream(config.newsConfig.kafkaConfig)

          val streamFxcm = fxcmKafkaStream
            .transform(
              fcxmTransformation)
          val streamTwitter = twitterKafkaStream
            .transform(
              twitterTransformation)
          val streamCalendar = calendarKafkaStream
            .transform(
              calendarTransformation)
          val streamNews = newsKafkaStream
            .transform(
              newsTransformation)

          logger.info("Show mode configured and working")
          showStream(streamFxcm)
          showStream(streamCalendar)
          showStream(streamNews)
          showStream(streamTwitter).awaitTermination()
        }
    }
  }

  def showStream[A](df: Dataset[A]): StreamingQuery = {
    df.writeStream.option("numRows",10).option("truncate",false)
      .format("console")
      .start()
  }
}
