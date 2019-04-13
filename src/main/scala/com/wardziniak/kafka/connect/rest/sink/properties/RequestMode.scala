package com.wardziniak.kafka.connect.rest.sink.properties

object RequestMode extends Enumeration {
  type RequestMode = Value
  val splitted, grouped = Value
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
