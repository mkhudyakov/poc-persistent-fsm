object Dependencies {
  import sbt._

  /**
   * Contains all Scala dependencies
   */
  object scala {
    val scala_version = "2.11.7"

    val reflection = "org.scala-lang" % "scala-reflect" % scala_version
    val compiler = "org.scala-lang" % "scala-compiler" % scala_version
  }

  object akka {
    private val akka_version = "2.4.2"

    val actor   = "com.typesafe.akka" %% "akka-actor"   % akka_version
    val kernel  = "com.typesafe.akka" %% "akka-kernel"  % akka_version
    val persist = "com.typesafe.akka" %% "akka-persistence" % akka_version
    val testkit = "com.typesafe.akka" %% "akka-testkit" % akka_version
    val slf4j   = "com.typesafe.akka" %% "akka-slf4j"   % akka_version
    val contrib = "com.typesafe.akka" %% "akka-contrib" % akka_version intransitive()

    val cluster             = "com.typesafe.akka" %% "akka-cluster" % akka_version
    val clusterSharding     = "com.typesafe.akka" %% "akka-cluster-sharding" % akka_version

    val persistLevelDB      = "org.iq80.leveldb" % "leveldb" % "0.7"
    val persistCassandra    = "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.11"
    val chill               = "com.twitter"           %% "chill-akka"                 % "0.5.0" exclude("com.esotericsoftware.minlog", "minlog")
  }

  /**
   * All Spray dependencies
   */
  object spray {
    private val spray_version = "1.3.1"

    val can     = "io.spray"  %% "spray-can"     % spray_version
    val http    = "io.spray"  %% "spray-http"    % spray_version
    val httpx   = "io.spray"  %% "spray-httpx"   % spray_version
    val routing = "io.spray"  %% "spray-routing" % spray_version
    val client  = "io.spray"  %% "spray-client"  % spray_version
    val testkit = "io.spray"  %% "spray-testkit" % spray_version
    
    val json    = "io.spray" %% "spray-json"    % "1.2.6"
  }

  /**
   * Apache Commons dependencies
   */
  object commons {
    val codec = "commons-codec"           % "commons-codec"         % "1.9"
    val io =    "commons-io"              % "commons-io"            % "2.4"
    val lang3 = "org.apache.commons"      % "commons-lang3"         % "3.3.2"

  }

  object scalaz {
    private val scalaz_version = "7.0.6"
    val core     = "org.scalaz"      %% "scalaz-core"           % scalaz_version
    val effect   = "org.scalaz"      %% "scalaz-effect"         % scalaz_version
    val iteratee = "org.scalaz"      %% "scalaz-iteratee"       % scalaz_version
  }

  object testing {
    val specs2_old = "org.specs2"     %% "specs2"     % "1.13"
    val specs2     = "org.specs2"     %% "specs2"     % "2.3.13"
    val scalacheck = "org.scalacheck" %% "scalacheck" % "1.11.3"
  }

  object cassandra {
    val driver_core   = "com.datastax.cassandra"  % "cassandra-driver-core" % "2.1.5"  excludeAll(
      ExclusionRule("org.slf4j", "slf4j-log4j12"),
      ExclusionRule("org.slf4j", "slf4j-jdk14")
    )
    val snappy_java   = "org.xerial.snappy"       % "snappy-java"           % "1.0.5"
  }
  
  val jodatime         = "joda-time"            % "joda-time"           % "2.4"
  val jodaconvert      = "org.joda"             % "joda-convert"        % "1.7"
  val logback          = "ch.qos.logback"       % "logback-classic"     % "1.1.2"
}