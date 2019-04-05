import sbt.{ModuleID, _}

object Dependencies {


  val KafkaClient: ModuleID = "org.apache.kafka" % "kafka-clients" % Versions.Kafka
  val KafkaConnectApi: ModuleID = "org.apache.kafka" % "connect-api" % Versions.Kafka
}
