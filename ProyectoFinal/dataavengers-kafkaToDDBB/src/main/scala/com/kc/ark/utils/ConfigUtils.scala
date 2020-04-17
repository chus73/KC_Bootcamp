package com.kc.ark.utils

import cats.syntax.either._
import com.typesafe.config.ConfigFactory
import com.kc.ark.config.AppConfig
import com.kc.ark.utils.HDFSUtils.implicits._
import com.kc.ark.config.reader.configUtils.implicits._
import com.kc.ark.config.implicits._
import org.apache.hadoop.fs.FileSystem
import org.apache.spark.sql.SparkSession

object ConfigUtils {
  /**
    * Reads the configuration from hdfs, getting the hadoop config from the spark session
    * @param pathToConfig the path to read in hdfs
    * @param sparkSession the spark session to use
    * @return the application configuration or the corresponding error
    */
  def getConfig(pathToConfig:String)(implicit sparkSession: SparkSession):Either[Throwable,AppConfig] =
    for {
      fileSystem <- Either.catchNonFatal(FileSystem.get(sparkSession.sparkContext.hadoopConfiguration))
      file <- fileSystem.readTextFile(pathToConfig)
      genConf <- Either.catchNonFatal(ConfigFactory.parseString(file))
      resolvedConf <- Either.catchNonFatal(genConf.resolve())
      appConfig <- resolvedConf.to[AppConfig]
    } yield appConfig

}
