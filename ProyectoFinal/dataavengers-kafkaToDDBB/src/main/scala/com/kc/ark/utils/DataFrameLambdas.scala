package com.kc.ark.utils

import org.apache.spark.sql.{DataFrame, Dataset, Encoder}

object DataFrameLambdas {

  def to[A:Encoder]:DataFrame => Dataset[A] = df => {
    df.as[A]
  }
}
