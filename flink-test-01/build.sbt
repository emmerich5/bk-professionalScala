name := "flink-test-01"

version := "0.1"

scalaVersion := "2.12.9"

exportJars := true

libraryDependencies ++= Seq(
    "org.apache.flink" % "flink-core" % "1.8.1",
    "org.apache.flink" % "flink-scala_2.12" % "1.8.1",
    "org.apache.flink" % "flink-streaming-scala_2.12" % "1.8.1",
    "org.apache.flink" % "flink-clients_2.12" % "1.8.1",
    "org.asynchttpclient" % "async-http-client" % "2.4.9"
)