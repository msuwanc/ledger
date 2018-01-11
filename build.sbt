name := "ledger"

version := "0.1"

scalaVersion := "2.12.4"

val circeVersion = "0.9.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.google.inject" % "guice" % "4.1.0"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
)
libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.18.1"