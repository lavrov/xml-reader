name := "xml-reader"

organization := "com.github.lavrov"

version := "0.2"

scalaVersion := "2.12.3"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.9.0",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)