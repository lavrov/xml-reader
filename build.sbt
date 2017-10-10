name := "xml-reader"

organization := "nl.elmar"

version := "0.4"

scalaVersion := "2.12.3"

crossScalaVersions := Seq("2.11.8", scalaVersion.value)

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats" % "0.9.0",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)

homepage := Some(url("https://github.com/elmarreizen/xml-reader"))

licenses := Seq("Apache 2.0" -> url("http://opensource.org/licenses/Apache-2.0"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/elmarreizen/xml-reader"),
    "scm:git@github.com:elmarreizen/xml-reader.git"
  )
)

developers := List(
  Developer(
    id    = "lavrov",
    name  = "Vitaly Lavrov",
    email = "vlavrov@elmar.nl",
    url   = url("https://github.com/lavrov")
  )
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

releasePublishArtifactsAction := PgpKeys.publishSigned.value

import ReleaseTransformations._

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  releaseStepCommand(Sonatype.SonatypeCommand.sonatypeRelease),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
