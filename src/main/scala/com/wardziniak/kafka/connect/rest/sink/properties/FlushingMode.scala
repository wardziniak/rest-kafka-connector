package com.wardziniak.kafka.connect.rest.sink.properties

object FlushingMode extends Enumeration {
  type FlushingMode = Value
  val message, poll, commit = Value
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
