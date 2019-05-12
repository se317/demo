name := "FlipayDemo"

version := "0.1"

scalaVersion := "2.12.8"

//Akka
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.22",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.22"
)

//Akka HTTP
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.8",
  "de.heikoseeberger" %% "akka-http-circe" % "1.25.2"
)

//ScalaTest
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

//Circe
val circeVersion = "0.11.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic-extras",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-optics",
  "io.circe" %% "circe-java8"
).map(_ % circeVersion)

//Logging/Config
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
libraryDependencies += "com.typesafe" % "config" % "1.3.2"

//Enumeratum
libraryDependencies ++= Seq(
  "com.beachape" %% "enumeratum" % "1.5.13"
)
