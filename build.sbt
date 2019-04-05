ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "com.lunchlist"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.10"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

scalacOptions ++= Seq("-feature", "-language:postfixOps")

run / fork := true