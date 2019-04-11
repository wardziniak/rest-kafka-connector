package com.wardziniak.kafka.connect.rest.sink

import com.softwaremill.sttp.{HttpURLConnectionBackend, Id, SttpBackend}
import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.kafka.connect.rest.sink.SinkWriter.{CommitFlush, EachRecordFlush, FlushType, PollFlush}
import org.apache.kafka.connect.sink.SinkRecord

import scala.collection.mutable.ArrayBuffer

trait SinkWriter
  extends LazyLogging {

  val flushType: FlushType = PollFlush

  private val bufferRecords: ArrayBuffer[SinkRecord] = ArrayBuffer()

  def addRecord(record: SinkRecord): Unit = {
    bufferRecords.append(record)
    logger.debug(s"addRecord:$record")
  }

  final def flushMessage(): Unit = {
    logger.debug("flushForEachMessage")
    flushRecords(EachRecordFlush)
  }

  final def flushPoll(): Unit = {
    logger.debug("flushForEachPoll")
    flushRecords(PollFlush)
  }

  final def flushCommit(): Unit = {
    logger.debug("flushForEachCommit")
    flushRecords(CommitFlush)
  }

  def flush(recordsToFlush: Seq[SinkRecord]): Boolean

  private def flushRecords(flushType: FlushType): Boolean = {
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

object SinkWriter {
  sealed trait FlushType
  object EachRecordFlush extends FlushType
  object PollFlush extends FlushType
  object CommitFlush extends FlushType
}
