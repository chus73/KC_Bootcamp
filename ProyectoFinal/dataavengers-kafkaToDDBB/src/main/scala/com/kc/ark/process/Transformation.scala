package com.kc.ark.process

import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Column, DataFrame, SparkSession}
import org.apache.spark.sql.catalyst.ScalaReflection.universe.TypeTag
import org.apache.spark.sql._

case class Transformation (){

  import com.kc.ark.process.TransformationProcess._




  /*Fcxm*/
  def processFcxmMessage[T: Encoder : TypeTag](implicit sparkSession: SparkSession): DataFrame => DataFrame =
    parseToJsoncc[T] andThen flatting("candles","valCandle") andThen selectColumnAndDrop("valCandle") andThen drop(fcxmColumnsToDrop)

  private val fcxmColumnsToDrop: List[Column] = List("instrumentId").map(col)

  private val calendarColumnsToDrop: List[Column] = List("id").map(col)


  /*Twitter*/
  def processTwitterMessage[T: Encoder : TypeTag](implicit sparkSession: SparkSession): DataFrame => DataFrame =
    parseToJsoncc[T] andThen keysGenerator(aditionalIDKeys) andThen  drop(List("id").map(col))

  val aditionalIDKeys: List[(String, Column)] = {
    List(
      "_id" -> col("id")
    )
  }


  /*Calendar*/
  def processCalendarMessage[T: Encoder : TypeTag](implicit sparkSession: SparkSession): DataFrame => DataFrame =
    parseToJsoncc[T] andThen drop(calendarColumnsToDrop) andThen flatting("events","valEvents") andThen selectColumnAndDrop("valEvents") andThen (keysGenerator(aditionalIDKeys)) andThen drop(calendarColumnsToDrop)

  def processNewsMessage[T: Encoder : TypeTag](implicit sparkSession: SparkSession): DataFrame => DataFrame =
    parseToJsoncc[T]

}
