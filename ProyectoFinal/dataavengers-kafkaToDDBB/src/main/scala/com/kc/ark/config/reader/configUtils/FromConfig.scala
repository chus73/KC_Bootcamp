package com.kc.ark.config.reader.configUtils

import com.typesafe.config.{Config, ConfigValue}
import cats.syntax.either._
import com.kc.ark.config.reader.configUtils.FromConfig.ConfigElement

object FromConfig {
  type ConfigElement[A] = Either[Throwable,A]
}

trait FromConfig[A] {
  def get(tag:String)(config:Config):ConfigElement[A]
}
trait FromConfigComplex[A] extends FromConfig[A]{
  def fromComfig(config:Config):ConfigElement[A]
  def get(tag:String)(config:Config):ConfigElement[A] = {
    Either.catchNonFatal(config.getConfig(tag)).right.flatMap(fromComfig)
  }
}
