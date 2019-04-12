package com.wardziniak.kafka.connect.rest.sink

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.kafka.connect.rest.sink.properties.FlushingMode
import com.wardziniak.kafka.connect.rest.sink.properties.FlushingMode.FlushingMode
import org.apache.kafka.connect.sink.SinkRecord

import scala.collection.mutable.ArrayBuffer

trait SinkWriter
  extends LazyLogging {

  val flushType: FlushingMode

  private val bufferRecords: ArrayBuffer[SinkRecord] = ArrayBuffer()

  def addRecord(record: SinkRecord): Unit = {
    bufferRecords.append(record)
    logger.debug(s"addRecord:$record")
  }

  final def flushMessage(): Unit = {
    logger.debug("flushForEachMessage")
    flushRecords(FlushingMode.message)
  }

  final def flushPoll(): Unit = {
    logger.debug("flushForEachPoll")
    flushRecords(FlushingMode.poll)
  }

  final def flushCommit(): Unit = {
    logger.debug("flushForEachCommit")
    flushRecords(FlushingMode.commit)
  }

  def flush(recordsToFlush: Seq[SinkRecord]): Boolean

  private def flushRecords(flushType: FlushingMode): Boolean = {
    if (this.flushType == flushType) {
      val resultsStatus = flush(collection.immutable.Seq(bufferRecords: _*))
      if (resultsStatus)
        bufferRecords.clear()
      resultsStatus
    }
    else
      true
  }
}
