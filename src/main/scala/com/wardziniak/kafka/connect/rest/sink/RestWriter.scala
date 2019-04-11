package com.wardziniak.kafka.connect.rest.sink

import com.softwaremill.sttp._
import com.typesafe.scalalogging.LazyLogging
import org.apache.kafka.connect.json.{JsonConverter, JsonConverterConfig}
import org.apache.kafka.connect.sink.SinkRecord

import scala.collection.JavaConverters._
import java.util.{Map => JMap}

import org.apache.kafka.connect.storage.{ConverterConfig, ConverterType}

import scala.collection.mutable.ArrayBuffer

case class RestWriter(url: String)
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

  override def flush(recordsToFlush: Seq[SinkRecord]): Boolean = {
    logger.debug("flushRecords")
    implicit val backend: SttpBackend[Id, Nothing] = HttpURLConnectionBackend()
    val convertedRecords = recordsToFlush.map(record => new String(jsonConverter.fromConnectData("", record.valueSchema(), record.value())))
//    val aa: Request[String, Nothing] = sttp.get(uri)
//    val response = aa.send()
//    logger.debug(s"write: ${response.code}, ${response.unsafeBody}")

    import sinkRecordSerializer._

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
    true
  }
}
