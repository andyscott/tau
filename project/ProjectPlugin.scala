import com.typesafe.sbt.site.jekyll.JekyllPlugin.autoImport._
import com.typesafe.sbt.site.SitePlugin.autoImport._
import microsites.MicrositeKeys._
import sbt.Keys._
import sbt._
import sbtcrossproject.CrossProject
import sbtcrossproject.CrossPlugin.autoImport._
import scalajscrossproject.ScalaJSCrossPlugin.autoImport._
import scalanativecrossproject.ScalaNativeCrossPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport._

object ProjectPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins      = plugins.JvmPlugin

  object autoImport {

    def module(
        modName: String,
        prefix: String = "modules/"
    ): CrossProject =
      CrossProject(modName, file(s"$prefix$modName"))(
        JSPlatform,
        JVMPlatform
      )
        .crossType(CrossType.Pure)
        .withoutSuffixFor(JVMPlatform)
        .build()
        .jvmSettings(fork in Test := true)
        .settings(moduleName := s"tau-$modName")

    lazy val macroSettings: Seq[Setting[_]] = Seq(
      libraryDependencies ++= Seq(
        scalaOrganization.value % "scala-compiler" % scalaVersion.value % Provided,
        scalaOrganization.value % "scala-reflect"  % scalaVersion.value % Provided,
        compilerPlugin(
          "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.patch)
      ))

    lazy val noPublishSettings: Seq[Def.Setting[_]] = Seq(
      publish := ((): Unit),
      publishLocal := ((): Unit),
      publishArtifact := false)
  }

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      organization := "io.higherkindness",
      name := "tau",
      startYear := Option(2019),
      parallelExecution in Test := false,
      outputStrategy := Some(StdoutOutput),
      connectInput in run := true,
      cancelable in Global := true,
      crossScalaVersions := List("2.11.12", "2.12.8", "2.13.0"),
      scalaVersion := "2.13.0"
    ) ++ publishSettings

  lazy val publishSettings = Seq(
    releaseCrossBuild := true,
    homepage := Some(url("https://github.com/andyscott/tau")),
    licenses := Seq(
      "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := (_ => false),
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    autoAPIMappings := true,
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/andyscott/tau"),
        "scm:git:git@github.com:andyscott/tau.git"
      )
    ),
    developers := List(
      Developer(
        "andyscott",
        "Andy Scott",
        "andy.g.scott@gmail.com",
        url("https://twitter.com/andygscott")
      )
    )
  )

}
