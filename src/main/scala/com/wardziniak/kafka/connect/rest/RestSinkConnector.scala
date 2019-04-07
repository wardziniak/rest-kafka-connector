package com.wardziniak.kafka.connect.rest

import java.util.{ArrayList => JArrayList, List => JList, Map => JMap}

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector.Task
import org.apache.kafka.connect.sink.SinkConnector

import scala.collection.JavaConverters._

class RestSinkConnector
  extends SinkConnector
    with LazyLogging {

  var configProps: JMap[String, String] = _

  override def start(props: JMap[String, String]): Unit  = {
    logger.info(s"Starting with props: $props")
    this.configProps = props
  }

  override def taskClass(): Class[_ <: Task] = classOf[RestSinkTask]

  override def taskConfigs(maxTasks: Int): JList[JMap[String, String]] =
    (1 to maxTasks)
      .foldLeft(List[JMap[String, String]]())((acc, _) => configProps :: acc)
      .asJava

  override def stop(): Unit = {}

  override def config(): ConfigDef = RestSinkConfig.CONFIG_DEF

  override def version(): String = "0.1.0"
}
