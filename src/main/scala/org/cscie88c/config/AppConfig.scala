package org.cscie88c.config

import pureconfig.ConfigReader
import scala.reflect.ClassTag
import pureconfig.ConfigSource

object ConfigUtils {

  /** loads a configuration case class
    */
  def loadAppConfig[A: ConfigReader: ClassTag](path: String): A =
    ConfigSource.default.at(path).loadOrThrow[A]
}
