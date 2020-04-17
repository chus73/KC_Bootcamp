package com.kc.ark.utils

import com.kc.ark.config.MongoConfig
import com.mongodb.spark.config._
import com.mongodb.spark.sql._
import org.apache.spark.sql.Dataset
import org.slf4j.LoggerFactory

object MongoUtils {
  private val logger = LoggerFactory.getLogger(MongoUtils.getClass)

  def writeAsBatchMongo2[A](mongoConfig: MongoConfig)(ds: Dataset[A]): Unit = {
    val sinkConfigMap =
      Map(
        "uri" -> mongoConfig.uri,
        "database" -> mongoConfig.database,
        "collection" -> mongoConfig.collection,
        //"checkpointLocation" -> collectionConfig.checkpointPath,
        "maxBatchSize" -> mongoConfig.maxBatchSize.toString
        //"Upsert" -> "true"
      )

    ds.write
      .format("com.mongodb.spark.sql.DefaultSource")
      .mode("append")
      .options(sinkConfigMap).save()
  }

  def writeAsBatchMongo[A](mongoConfig: MongoConfig)(ds: Dataset[A]): Unit = {
    val sinkConfigMap = WriteConfig(
      Map(
        "uri" -> mongoConfig.uri,
        "database" -> mongoConfig.database,
        "collection" -> mongoConfig.collection,
        //"checkpointLocation" -> collectionConfig.checkpointPath,
        "maxBatchSize" -> mongoConfig.maxBatchSize.toString
        //"Upsert" -> "true"
      ))
    ds.write
      .mode("overwrite")
      .mongo(sinkConfigMap)
  }
}
