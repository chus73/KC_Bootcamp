package com.kc.ark.model

case class FcxmInput(instrumentId: String,
                     instrument: String,
                     periodId: String,
                     candles: List[Candles])

case class Candles(timestamp: Long,
                   bidOpen:Double,
                   bidClose:Double,
                   bidHigh:Double,
                   bidLow:Double,
                   askOpen:Double,
                   askClose:Double,
                   askHigh:Double,
                   askLow:Double,
                   tickqty:Int,
                   dateFormatted:String
                  )