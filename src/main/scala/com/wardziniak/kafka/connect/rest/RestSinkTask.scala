package com.wardziniak.kafka.connect.rest

import java.util.{Collection => JCollection, Map => JMap}

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.kafka.connect.rest.sink.properties.FlushingMode.FlushingMode
import com.wardziniak.kafka.connect.rest.sink.{RestSinkConfig, RestWriter}
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.sink.{SinkRecord, SinkTask}

class RestSinkTask
  extends SinkTask
    with LazyLogging {

  var restUrl: String = _
  var flushingMode: FlushingMode = _
  var writer: RestWriter = _

  override def start(props: JMap[String, String]): Unit = {
    val sinkConfig = RestSinkConfig(props)
    restUrl = sinkConfig.getRestUrl
    flushingMode = sinkConfig.getFlushingMode
    writer = RestWriter(restUrl, flushingMode)
  }

  override def put(records: JCollection[SinkRecord]): Unit = {
    records.forEach(record => {
      writer.addRecord(record)
      writer.flushMessage()
    })
    writer.flushPoll()
  }

  override def flush(currentOffsets: JMap[TopicPartition, OffsetAndMetadata]): Unit = {
    super.flush(currentOffsets)
    writer.flushCommit()
  }

  override def stop(): Unit = {}

  override def version(): String = "0.1.0"
}
