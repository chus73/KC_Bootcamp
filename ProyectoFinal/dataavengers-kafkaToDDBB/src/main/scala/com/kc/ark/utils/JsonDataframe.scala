package com.kc.ark.utils

import org.apache.spark.sql.catalyst.ScalaReflection
import org.apache.spark.sql.{Column}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.catalyst.ScalaReflection.universe.TypeTag
import org.apache.spark.sql._

object JsonDataframe {
  def from_jsoncc[T: Encoder : TypeTag](column: Column): Column = {
    val schema = ScalaReflection.schemaFor[T].dataType.asInstanceOf[StructType]
    org.apache.spark.sql.functions.from_json(column, schema)
  }
}
