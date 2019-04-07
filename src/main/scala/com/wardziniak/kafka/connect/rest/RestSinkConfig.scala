package com.wardziniak.kafka.connect.rest

import com.wardziniak.kafka.connect.rest.RestSinkConfig.RestUrlProperty
import org.apache.kafka.common.config.{AbstractConfig, ConfigDef}

import java.util.{Map => JMap}

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

  trait ConfigProperty {
    val Name: String
    val Default: String
    val Doc: String
    val Display: String
  }
}
