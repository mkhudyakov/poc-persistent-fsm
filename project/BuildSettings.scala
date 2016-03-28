import com.typesafe.sbt.SbtNativePackager

object BuildSettings {
  import sbt._
  import Keys._
  import com.typesafe.sbt.packager.Keys._
  import com.typesafe.sbt.SbtNativePackager.Docker
  import org.scalastyle.sbt.ScalastylePlugin._
  import sbtrelease.ReleasePlugin._
  import com.typesafe.sbt.SbtNativePackager.packageArchetype

  lazy val buildSettings =
    Defaults.defaultSettings ++
      packageArchetype.java_server ++
      Seq(
        scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.7",
          "-deprecation", "-unchecked",
          "-Ywarn-dead-code"), // "-Xfatal-warnings"),
        scalacOptions in (Compile, doc) <++= (name in (Compile, doc), version in (Compile, doc)) map DefaultOptions.scaladoc,
        javaOptions += "-Xmx4G",
        outputStrategy := Some(StdoutOutput),
        fork := true,
        resolvers ++= Seq(
          Resolver.mavenLocal,
          "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
          "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
          Resolver.sonatypeRepo("releases"),
          Resolver.sonatypeRepo("snapshots"),
          "Spray Releases" at "http://repo.spray.io",
          "Jasper Reports" at "http://jasperreports.sourceforge.net/maven2"
        ),
        parallelExecution in Test := false,
        shellPrompt := { st => Project.extract(st).currentProject.id + "> " },
        /* Publish to local repository */
        publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))
      )
}
