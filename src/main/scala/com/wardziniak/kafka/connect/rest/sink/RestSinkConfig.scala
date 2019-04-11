package com.wardziniak.kafka.connect.rest.sink

import java.util.{Map => JMap}

import com.wardziniak.kafka.connect.rest.sink.RestSinkConfig.RestUrlProperty
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

case class RestSinkConfig(props: JMap[String, String]) extends AbstractConfig(RestSinkConfig.CONFIG_DEF, props) {
  val restURL: String = getString(RestUrlProperty.Name)
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


  object RestUrlProperty extends ConfigProperty {
    override val Name = "rest.url"
    override val Default = ""
    override val Doc = ""
    override val Display: String = "Rest server URL"
  }

  object PutModeProperty extends ConfigProperty {
    override val Name: String = "insert.mode"
    override val Default: String = "bulk"
    override val Doc: String = ""
    override val Display: String =
      """
        |Insertion types:
        | bigbulk - message are gather in batches for each commit
        | smallbulk - for each portion returned by KafkaConsumer::pool(...) is inserted as one batch
        | single - each message is put by single rest call
      """.stripMargin
  }

  trait ConfigProperty {
    val Name: String
    val Default: String
    val Doc: String
    val Display: String
  }
}
