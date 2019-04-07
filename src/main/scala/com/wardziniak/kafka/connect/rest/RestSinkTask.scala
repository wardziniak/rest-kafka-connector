package com.wardziniak.kafka.connect.rest

import java.util.{Collection => JCollection, Map => JMap}

import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.connect.sink.{SinkRecord, SinkTask}

class RestSinkTask
  extends SinkTask
    with LazyLogging {

  var restUrl: String = _


  override def start(props: JMap[String, String]): Unit = {
    val sinkConfig = RestSinkConfig(props)
    restUrl = sinkConfig.restURL
  }

  override def put(records: JCollection[SinkRecord]): Unit = {
    records.forEach(record => logger.debug(s"Put $record"))
  }

  override def stop(): Unit = {}

  override def version(): String = "0.1.0"
}
