package com.wardziniak.kafka.connect.rest.sink

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.kafka.connect.rest.sink.properties.FlushingMode.FlushingMode
import com.wardziniak.kafka.connect.rest.sink.properties.RequestMode
import com.wardziniak.kafka.connect.rest.sink.properties.RequestMode.RequestMode
import org.apache.kafka.connect.json.{JsonConverter, JsonConverterConfig}
import org.apache.kafka.connect.sink.SinkRecord

import scala.collection.JavaConverters._

case class RestWriter(url: String, flushType: FlushingMode, requestMode: RequestMode)
  extends SinkWriter
  with LazyLogging {

  val uri: Uri = uri"http://localhost:9000/v1/posts"

  val request: Request[String, Nothing] = sttp.get(uri"http://localhost:9000/v1/posts")

  //val bufferRecords: ArrayBuffer[SinkRecord] = ArrayBuffer()

  val jsonConverter = new JsonConverter()
  jsonConverter.configure(Map[String, String](
    JsonConverterConfig.SCHEMAS_ENABLE_CONFIG -> false.toString,
    JsonConverterConfig.SCHEMAS_CACHE_SIZE_CONFIG -> JsonConverterConfig.SCHEMAS_CACHE_SIZE_DEFAULT.toString
  ).asJava, false)


  implicit val sinkRecordSerializer: BodySerializer[SinkRecord] = { record: SinkRecord =>
    val serialized = new String(jsonConverter.fromConnectData("", record.valueSchema(), record.value()))
    StringBody(serialized, "UTF-8", Some("application/json"))
  }

  implicit val sinkListRecordSerializer: BodySerializer[Seq[SinkRecord]] = { records: Seq[SinkRecord] =>
    val serializedBody = s"[${records.map(record => jsonConverter.fromConnectData("", record.valueSchema(), record.value()))
      .map(new String(_))
      .mkString(",")}]"
    StringBody(serializedBody, "UTF-8", Some("application/json"))
  }

  override def flush(recordsToFlush: Seq[SinkRecord]): Boolean = {
    logger.debug("flushRecords")
    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
    val convertedRecords: Seq[String] = recordsToFlush.map(record => new String(jsonConverter.fromConnectData("", record.valueSchema(), record.value())))

    logger.debug(s"requestMode=$requestMode")
    logger.debug(s"number of records ${recordsToFlush.size}")

    if (recordsToFlush.nonEmpty) {
      if (requestMode == RequestMode.grouped) {
        val req1 = sttp.body(recordsToFlush)
        val req2 = req1.post(uri"http://localhost:9000/v1/posts/posts")
        val req3 = req2.send()
        logger.debug(s"Code: ${req3.code}")
      }
      else {
        val codes: Seq[StatusCode] = recordsToFlush.map(convertedRecord => {
          val req1 = sttp.body(convertedRecord)
          val req2 = req1.post(uri)
          val req3 = req2.send()
          req3
        })
          .map(_.code)
        convertedRecords.foreach(convertedRecord => logger.debug(s"Body[$convertedRecord"))
        codes.foreach(code => logger.debug(s"Code: $code"))
        logger.debug(s"Payload: [$convertedRecords],[${convertedRecords.size}")
        logger.debug(s"Payload: [$convertedRecords],[${convertedRecords.size}")
      }
    }

//    val codes: Seq[StatusCode] = recordsToFlush.map(convertedRecord => {
//      val req1 = sttp.body(convertedRecord)
//      val req2 = req1.post(uri)
//      val req3 = req2.send()
//      req3
//    })
//      .map(_.code)
//    convertedRecords.foreach(convertedRecord => logger.debug(s"Body[$convertedRecord"))
//    codes.foreach(code => logger.debug(s"Code: $code"))
//    logger.debug(s"Payload: [$convertedRecords],[${convertedRecords.size}")
    true
  }
}
