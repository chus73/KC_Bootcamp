package com.kc.ark

import com.typesafe.config.Config
import com.kc.ark.config.reader.configUtils.{FromConfig, FromConfigComplex}
import com.kc.ark.config.reader.configUtils.FromConfig.ConfigElement

package object config {

  import com.kc.ark.config.reader.configUtils.implicits._
  import cats.syntax.either._

  /**
    * This elements allows to parse the configuration, just
    */
  object implicits {

    /**
      * The implicit to read a KafkaConfig object
      */
    implicit val kafkaConfigImpl: FromConfigComplex[KafkaConfig] =
      new FromConfigComplex[KafkaConfig] {
        override def fromComfig(config: Config): ConfigElement[KafkaConfig] =
          for {
            bootstrapServers <- config.get[String]("bootstrapServers")
            topics <- config.get[List[String]]("topics")
            maxOffSetsPerBatch <- config.get[Int]("maxOffSetsPerBatch")
            startingOffsets <- config.get[Option[String]]("startingOffsets")
          } yield KafkaConfig(bootstrapServers, topics, maxOffSetsPerBatch, startingOffsets)
      }

    /**
      * The implicit to read a MongoConfig object
      */
    implicit val mongoConfigImpl: FromConfigComplex[MongoConfig] =
      new FromConfigComplex[MongoConfig] {
        override def fromComfig(config: Config): ConfigElement[MongoConfig] =
          for {
            uri <- config.get[String]("uri")
            database <- config.get[String]("database")
            ammountPerBatch <- config.get[Int]("ammountPerBatch")
            collection <- config.get[String]("collection")
            checkpointPath <- config.get[String]("checkpointPath")
          } yield MongoConfig(uri, database, ammountPerBatch,collection,checkpointPath)
      }


    /**
      * The implicit to read a AppConfig object
      */
    implicit val appConfig: FromConfigComplex[AppConfig] = new FromConfigComplex[AppConfig] {
      override def fromComfig(config: Config): ConfigElement[AppConfig] =
        for {
          fxcmConfig <- config.get[APIConfig]("fxcmConfig")
          calendarConfig <- config.get[APIConfig]("calendarConfig")
          newsConfig <- config.get[APIConfig]("newsConfig")
          twitterConfig <- config.get[APIConfig]("twitterConfig")
        } yield AppConfig(fxcmConfig, calendarConfig,newsConfig,twitterConfig)
    }

    /**
      * The implicit to read a AppConfig object
      */
    implicit val apiConfig: FromConfigComplex[APIConfig] = new FromConfigComplex[APIConfig] {
      override def fromComfig(config: Config): ConfigElement[APIConfig] =
        for {
          kafkaConfig <- config.get[KafkaConfig]("kafkaConfig")
          mongoConfig <- config.get[MongoConfig]("mongoConfig")
        } yield APIConfig(kafkaConfig, mongoConfig)
    }
  }

}
