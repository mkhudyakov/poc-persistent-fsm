import sbt._
import Keys._

object PersistentFsmPocBuild extends Build {

  import Dependencies._
  override val settings = super.settings ++ Seq(
    organization := "com.hakka.fsm.poc",
    version := "1.0-SNAPSHOT",
    scalaVersion := scala.scala_version
  )

  def module(dir: String, extraSettings: Seq[Setting[_]] = Nil) = Project(id = dir, base = file(dir),
    settings = BuildSettings.buildSettings ++ extraSettings)

  lazy val root = Project(
    id = "parent",
    base = file("."),
    settings = BuildSettings.buildSettings ++ Seq(
      fork := true,
      connectInput in run := true,
      mainClass in (Compile, run) := Some("com.hakka.fsm.poc.Server"),

      libraryDependencies += akka.slf4j,
      libraryDependencies += akka.kernel,
      libraryDependencies += logback
    ),
    aggregate = Seq(core, api, data, formats)) dependsOn(data, formats, core, api)

  /**
   * JSON marshaling / unmarshaling
   */
  lazy val formats = module("formats") settings (
    libraryDependencies += akka.actor,
    libraryDependencies += akka.contrib,

    libraryDependencies += spray.can,
    libraryDependencies += spray.httpx,
    libraryDependencies += spray.routing,
    libraryDependencies += spray.client,
    libraryDependencies += spray.json,

    libraryDependencies += testing.specs2 % "test",
    libraryDependencies += testing.scalacheck % "test"
    ) dependsOn data

  lazy val data = module("data") settings (
    libraryDependencies += akka.actor,
    
    libraryDependencies += spray.can,
    libraryDependencies += spray.http,
    libraryDependencies += spray.httpx,

    libraryDependencies += scalaz.core,
    libraryDependencies += scalaz.iteratee,
    
    libraryDependencies += akka.persist,
    libraryDependencies += akka.persistCassandra,
    libraryDependencies += akka.persistLevelDB,
    libraryDependencies += akka.chill,

    libraryDependencies += commons.codec,

    libraryDependencies += jodatime,
    libraryDependencies += jodaconvert,

    libraryDependencies += testing.specs2 % "test",
    libraryDependencies += testing.scalacheck % "test"
    )

  lazy val core = module("core") settings (
    libraryDependencies += scala.reflection,
    libraryDependencies += scala.compiler,

    libraryDependencies += scalaz.core,
    libraryDependencies += scalaz.iteratee,

    libraryDependencies += akka.actor,
    libraryDependencies += akka.cluster,
    libraryDependencies += akka.clusterSharding,
    libraryDependencies += akka.contrib,
    libraryDependencies += akka.kernel,
    
    libraryDependencies += akka.persist,
    libraryDependencies += akka.persistCassandra,
    libraryDependencies += akka.persistLevelDB,
    libraryDependencies += akka.chill,
    
    libraryDependencies += cassandra.driver_core,

    libraryDependencies += commons.lang3,

    libraryDependencies += jodatime,
    libraryDependencies += jodaconvert,

    libraryDependencies += testing.specs2 % "test",
    libraryDependencies += testing.scalacheck % "test",
    libraryDependencies += akka.testkit  % "test"
  ) dependsOn(data, formats)

  lazy val api = module("api") settings (
    libraryDependencies += akka.actor,
    libraryDependencies += akka.kernel,
    
    libraryDependencies += akka.persist,
    libraryDependencies += akka.persistCassandra,
    libraryDependencies += akka.persistLevelDB,
    libraryDependencies += akka.chill,

    libraryDependencies += akka.contrib,
    libraryDependencies += spray.can,
    libraryDependencies += spray.httpx,
    libraryDependencies += spray.routing,
    libraryDependencies += spray.client,
    libraryDependencies += spray.json,

    libraryDependencies += commons.codec,

    libraryDependencies += spray.testkit % "test"
    ) dependsOn(core % "compile;test->test", data, formats)
}
