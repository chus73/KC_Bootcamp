package com.kc.ark.utils

import java.io.{BufferedReader, InputStreamReader}

import org.apache.hadoop.fs.{FileSystem, Path}

object HDFSUtils {
  object implicits {
    implicit class HDFSFileRead(fs:FileSystem) {
      import cats.implicits._
      def readTextFile(path:String):Either[Throwable,String] =
        for {
          hadoopPath <- Either.catchNonFatal(new Path(path))
          buffer <- Either.catchNonFatal(new BufferedReader(new InputStreamReader(fs.open(hadoopPath))))
          str <- Either.catchNonFatal(Stream.continually(buffer.readLine()).takeWhile(_ != null).mkString("\n"))
        } yield str
    }
  }
}
