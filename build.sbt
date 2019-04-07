import Dependencies._

name := "rest-kafka-connector"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  KafkaConnectApi, ScalaLogging, LogbackClassic, SttpCore
)
