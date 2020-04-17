package com.kc.ark.config

import com.kc.ark.config.ArgumentsParser.argumentsTags.{configPathTag, modeTag}
import com.kc.ark.process.Modes
import cats.syntax.either._

object ArgumentsParser {
  object argumentsTags {
    val configPathTag = "configPath"
    val modeTag = "mode"
  }

  /**
    * Parse the arguments and writes them in a Map
    * @param args the program arguments
    * @return a map with the arguments or the error
    */
  def parseArguments(args:Array[String]):Either[Throwable,Map[String,String]] = {
    Either.catchNonFatal{
      Map(modeTag -> args.lift(1).getOrElse(Modes.StreamingMode),
        configPathTag -> args(0)
      )
    }
  }
}
