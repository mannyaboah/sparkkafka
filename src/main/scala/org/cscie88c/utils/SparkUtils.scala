package org.cscie88c.utils

import org.apache.spark.SparkConf

/** Utility for setting spart conf properties
  */
object SparkUtils {

  def SparkConf(
      appName: String = "spark-dstream-app",
      masterUrl: String = "local[*]"
  ): SparkConf = {

    lazy val spark = new SparkConf().setMaster(masterUrl).setAppName(appName)

    spark
  }
}
