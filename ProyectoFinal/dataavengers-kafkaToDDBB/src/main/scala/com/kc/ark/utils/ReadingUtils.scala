package com.kc.ark.utils

import com.kc.ark.config.KafkaConfig
import org.apache.spark.sql.{DataFrame, SparkSession}


object ReadingUtils {
  object implicits {
    implicit class KafkaReader(sparkSession: SparkSession) {
      def fromKafka(implicit kafkaConfig: KafkaConfig): DataFrame = {
        val kafkaOptions = {
          val basicConfig = Map(
            "kafka.bootstrap.servers" -> kafkaConfig.bootstrapServers,
            "subscribe" -> kafkaConfig.topics.mkString(","),
            "maxOffsetsPerTrigger" -> kafkaConfig.maxOffSetsPerBatch.toString
          )
          kafkaConfig.startingOffsets.fold(basicConfig)(offsets =>
            basicConfig + ("startingOffsets" -> offsets))
        }

        sparkSession.readStream
          .format("kafka")
          .options(kafkaOptions)
          .load()
      }
    }
  }
}
