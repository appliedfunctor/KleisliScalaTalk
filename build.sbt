name := "Kleisli"

version := "0.1"

scalaVersion := "2.12.6"

scalacOptions += "-Ypartial-unification"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "1.0.1",
  "org.scalaz" %% "scalaz-core" % "7.2.23",
  "org.scalaz" %% "scalaz-concurrent" % "7.2.23",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.typelevel" %% "cats-effect" % "1.0.0-RC"
)
