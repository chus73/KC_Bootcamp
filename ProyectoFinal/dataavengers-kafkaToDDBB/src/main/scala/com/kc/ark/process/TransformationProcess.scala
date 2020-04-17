package com.kc.ark.process

import com.kc.ark.utils.JsonDataframe._
import org.apache.spark.sql.catalyst.ScalaReflection.universe.TypeTag
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, _}


object TransformationProcess {

  val ISODateTime = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
  val ISODateTimeCompress = "yyyyMMddHHmmssSSS"
  val ISODate = "yyyyMMdd"

  val drop: List[Column] => DataFrame => DataFrame = ls => ls.foldLeft(_)((df, column) => df.drop(column))

  val dropTmpColumns: DataFrame => DataFrame = df => {
    val columnsToDrop = df.columns.filter(_.endsWith("_TMP"))
    df.drop(columnsToDrop: _*)
  }

  val dropColumns: List[(String, Column)] => DataFrame => DataFrame =
    keysInfo => df => {
      //Drops columns that are in dataFrame and not in keysInfo
      val columnsToDrop = df.columns diff keysInfo.map(e => e._1)
      df.drop(columnsToDrop: _*)
    }

  def selectColumnAndDrop(column:String):DataFrame => DataFrame = df => {
    val x =df.select(column+".*").columns.map(element => (element -> col(column+"."+element)))
    x.foldLeft(df)((df, keyInfo) => df.withColumn(keyInfo._1, keyInfo._2)).drop(column)
  }

  private[process] def toDF[T]: Dataset[T] => DataFrame = _.toDF()

  private[process] val keysGenerator: List[(String, Column)] => DataFrame => DataFrame =
    keysInfo => df =>
      keysInfo.foldLeft(df)((df, keyInfo) => df.withColumn(keyInfo._1, keyInfo._2))

  private[process] def parseToJsoncc[T: Encoder : TypeTag]: DataFrame => DataFrame = ds => {
    ds.withColumn("parsed", from_jsoncc[T](ds.columns.map(col).head)).select(col("parsed.*"))
  }
  private[process] def flatting(column:String, newColumn:String):DataFrame => DataFrame = df => {
    df.withColumn(newColumn, explode(col(column))).drop(column)
  }
}
