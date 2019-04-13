package com.wardziniak.kafka.connect.rest.sink

import java.util.{Map => JMap}

import com.typesafe.scalalogging.LazyLogging
import com.wardziniak.kafka.connect.rest.sink.RestSinkConfig.{FlashingModeProperty, RequestModeProperty, RestUrlProperty}
import com.wardziniak.kafka.connect.rest.sink.properties.{FlushingMode, RequestMode}
import com.wardziniak.kafka.connect.rest.sink.properties.FlushingMode.FlushingMode
import com.wardziniak.kafka.connect.rest.sink.properties.RequestMode.RequestMode
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

case class RestSinkConfig(props: JMap[String, String])
  extends AbstractConfig(RestSinkConfig.CONFIG_DEF, props)
    with LazyLogging {
  logger.info(s"RestSinkConfig::$props")

  def getRestUrl: String = getString(RestUrlProperty.Name)
  def getFlushingMode: FlushingMode = FlushingMode.withNameOpt(getString(FlashingModeProperty.Name)).get
  def getRequestMode: RequestMode = RequestMode.withNameOpt(getString(RequestModeProperty.Name)).get
}

object RestSinkConfig {
  val RestGroup = "Rest"

  val CONFIG_DEF: ConfigDef = new ConfigDef()
    .define(RestUrlProperty.Name,
      ConfigDef.Type.STRING,
      RestUrlProperty.Default,
      ConfigDef.Importance.HIGH,
      RestUrlProperty.Doc,
      RestGroup,
      1,
      ConfigDef.Width.LONG,
      RestUrlProperty.Display
    )
    .define(FlashingModeProperty.Name,
      ConfigDef.Type.STRING,
      FlashingModeProperty.Default,
      ConfigDef.Importance.MEDIUM,
      FlashingModeProperty.Doc,
      RestGroup,
      2,
      ConfigDef.Width.LONG,
      FlashingModeProperty.Display
    )
    .define(RequestModeProperty.Name,
      ConfigDef.Type.STRING,
      RequestModeProperty.Default,
      ConfigDef.Importance.HIGH,
      RequestModeProperty.Doc,
      RestGroup,
      3,
      ConfigDef.Width.LONG,
      RequestModeProperty.Display
    )
    .define(RequestHeadersProperty.Name,
      ConfigDef.Type.STRING,
      RequestHeadersProperty.Default,
      ConfigDef.Importance.LOW,
      RequestHeadersProperty.Doc,
      RestGroup,
      4,
      ConfigDef.Width.LONG,
      RequestHeadersProperty.Display
    )

  object RestUrlProperty extends ConfigProperty {
    override val Name = "rest.url"
    override val Default = ""
    override val Doc = ""
    override val Display: String = "Rest server URL"
  }

  object FlashingModeProperty extends ConfigProperty {
    override val Name: String = "flashing.mode"
    override val Default: String = "commit"
    override val Doc: String = "flashing.mode"
    override val Display: String = "flashing.mode, possible values message/poll/commit"
  }
  object RequestModeProperty extends ConfigProperty {
    override val Name: String = "request.mode"
    override val Default: String = "single"
    override val Doc: String = "request.mode"
    override val Display: String = "request.mode, possible values single/grouped"
  }

  object RequestHeadersProperty extends ConfigProperty {
    override val Name: String = "request.headers"
    override val Default: String = ""
    override val Doc: String = "request.headers"
    override val Display: String = "request.headers, list of key value pairs separated with ';'. key and value are separated with '='."
  }

  trait ConfigProperty {
    val Name: String
    val Default: String
    val Doc: String
    val Display: String
  }
}
