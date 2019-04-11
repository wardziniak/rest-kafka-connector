import sbt.{ModuleID, _}

object Dependencies {


  val KafkaClient: ModuleID = "org.apache.kafka" % "kafka-clients" % Versions.Kafka
  val KafkaConnectApi: ModuleID = "org.apache.kafka" % "connect-api" % Versions.Kafka
  val KafkaConnectJson: ModuleID = "org.apache.kafka" % "connect-json" % Versions.Kafka
  val ScalaLogging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Versions.ScalaLogging
  val LogbackClassic: ModuleID = "ch.qos.logback" % "logback-classic" % Versions.LogbackClassic
  val SttpCore: ModuleID = "com.softwaremill.sttp" %% "core" % Versions.SttpCore
}
