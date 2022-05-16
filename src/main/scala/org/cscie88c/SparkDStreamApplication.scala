package org.cscie88c

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.Minutes
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.kafka010._
import org.cscie88c.config.ConfigUtils
import org.cscie88c.utils.SparkUtils
import pureconfig.generic.auto._

/** case class to facilitate config
  *
  * @param name
  * @param masterUrl
  */
case class SparkDStreamConfig(
    name: String,
    masterUrl: String
)

/** spark application: Connects to kafka and directly streams from the
  * thermostat topic
  */
object SparkDStreamApplication {

  val SPARK_DSTREAM_CONF_PATH = "org.cscie88c.spark-dstream-application"

  def main(args: Array[String]): Unit = {

    implicit val conf: SparkDStreamConfig = readConfig()

    val spark = SparkUtils.SparkConf(
      conf.name,
      conf.masterUrl
    )

    val sparkStreamingContext =
      new StreamingContext(spark, Minutes(1))

    sparkStreamingContext.sparkContext.setLogLevel("ERROR")

    val kafkaConfig = Map[String, Object](
      "client.dns.lookup" -> "resolve_canonical_bootstrap_servers_only",
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "group-id",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val kafkaTopics = Array("thermoTopic")

    val stream =
      KafkaUtils.createDirectStream[String, String](
        sparkStreamingContext,
        LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](kafkaTopics, kafkaConfig)
      )

    val thermostatStream =
      stream map (streamRecord => streamRecord.value().asInstanceOf[String])

    val recordsCount: DStream[Long] = thermostatStream.count()

    recordsCount.print()

    thermostatStream.foreachRDD(rdd =>
      rdd.coalesce(1, true).saveAsTextFile("data")
    )

    sparkStreamingContext.start()
    sparkStreamingContext.awaitTermination()
  }

  def readConfig(): SparkDStreamConfig =
    ConfigUtils.loadAppConfig[SparkDStreamConfig](SPARK_DSTREAM_CONF_PATH)
}
