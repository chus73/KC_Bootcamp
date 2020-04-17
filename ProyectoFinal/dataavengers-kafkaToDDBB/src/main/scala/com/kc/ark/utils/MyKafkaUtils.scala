package com.kc.ark.utils

import com.kc.ark.config.KafkaConfig
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.StringType
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream}


object MyKafkaUtils {
  def readAsBatch(kafkaConfig: KafkaConfig)(implicit sparkSession: SparkSession): DataFrame =
    sparkSession.read
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaConfig.bootstrapServers)
      .option("subscribe", kafkaConfig.topics.mkString(","))
      //.option("maxOffsetsPerTrigger", kafkaConfig.maxOffSetsPerBatch)
      .load()

  def readAsStream(kafkaConfig: KafkaConfig)(
                    implicit sparkSession: SparkSession): DataFrame =
    sparkSession.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaConfig.bootstrapServers)
      .option("subscribe", kafkaConfig.topics.mkString(","))
      .option("auto.offset.reset", kafkaConfig.startingOffsets.getOrElse("latest"))
      .option("maxOffsetsPerTrigger", "200")
      .load()

  def readAsStreamDStream(kafkaConfig: KafkaConfig, ssc:StreamingContext)(
                    implicit sparkSession: SparkSession): DStream[(String,String)] = {

    val kafkaParams = Map[String, Object](
      "key.deserializer" -> classOf[StringDeserializer],
      "bootstrap.servers" -> kafkaConfig.bootstrapServers,
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "dataavengers-spark-1",
      "auto.offset.reset" -> kafkaConfig.startingOffsets.getOrElse("latest"),
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](kafkaConfig.topics, kafkaParams)
    )

    stream.map(record => (record.key, record.value))
  }

  val extractValueFromKafkaMessage: DataFrame => DataFrame = { df =>
    df.select(col("value").cast(StringType))
  }

}
