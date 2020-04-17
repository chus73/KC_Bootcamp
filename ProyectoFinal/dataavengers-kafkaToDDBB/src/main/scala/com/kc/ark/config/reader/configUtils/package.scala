package com.kc.ark.config.reader

import cats.implicits._
import com.typesafe.config.{Config, ConfigList, ConfigValue}
import com.kc.ark.config.reader.configUtils.FromConfig.ConfigElement

import scala.collection.JavaConverters._

package object configUtils {

  object implicits {


    implicit val stringConfig: FromConfig[String] = new FromConfig[String] {
      override def get(tag: String)(config: Config): ConfigElement[String] =
        Either.catchNonFatal(config.getString(tag))
    }

    implicit val stringListConfig: FromConfig[List[String]] = new FromConfig[List[String]] {
      override def get(tag: String)(config: Config): ConfigElement[List[String]] =
        Either.catchNonFatal(config.getStringList(tag).asScala.toList)
    }

    implicit val intConfig: FromConfig[Int] = new FromConfig[Int] {
      override def get(tag: String)(config: Config): ConfigElement[Int] =
        Either.catchNonFatal(config.getInt(tag))
    }

    implicit val intListConfig: FromConfig[List[Int]] = new FromConfig[List[Int]] {
      override def get(tag: String)(config: Config): ConfigElement[List[Int]] =
        Either.catchNonFatal(config.getIntList(tag).asScala.toList.map(_.toInt))
    }

    implicit class ConfigOps(config: Config) {
      def get[A](tag: String)(implicit fromConfig: FromConfig[A]): ConfigElement[A] =
        fromConfig.get(tag)(config)
      def to[A](implicit fromConfigComplex: FromConfigComplex[A]):ConfigElement[A] =
        fromConfigComplex.fromComfig(config)
    }

    implicit def optionConfig[A](implicit fromConfigA: FromConfig[A]): FromConfig[Option[A]] =
      new FromConfig[Option[A]] {
        override def get(tag: String)(config: Config): ConfigElement[Option[A]] = {
          if (!config.hasPath(tag))
            none[A].asRight[Throwable]
          else
            fromConfigA.get(tag)(config).right.map(_.some)
        }
      }

    implicit def listConfig[A](implicit fromConfigA: FromConfigComplex[A]): FromConfig[List[A]] =
      new FromConfig[List[A]] {
        override def get(tag: String)(config: Config): ConfigElement[List[A]] =
          config.getConfigList(tag).asScala
            .foldLeft(List.empty[A].asRight[Throwable])((l, elem) => l.flatMap(parsed => fromConfigA.fromComfig(elem).right.map(h => h :: parsed)))
          .right.map(_.reverse)
      }

  }
}
