// Import xsbt-scalate-generator keys; this must be at top of build.sbt, or SBT will complain
import ScalateKeys._

organization := "takeharu.oshida"

name         := "xitrum-placeholder"

version      := "1.0-SNAPSHOT"

scalaVersion := "2.11.0"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Most Scala projects are published to Sonatype, but Sonatype is not default
// and it takes several hours to sync from Sonatype to Maven Central
resolvers += "SonatypeReleases" at "http://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "com.newrelic.agent.java" % "newrelic-agent" % "2.21.3"

libraryDependencies += "com.martiansoftware" % "jsap" % "2.1"

libraryDependencies += "tv.cntt" %% "xitrum" % "3.11"

// Xitrum uses SLF4J, an implementation of SLF4J is needed
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

// xgettext i18n translation key string extractor is a compiler plugin ---------

autoCompilerPlugins := true

addCompilerPlugin("tv.cntt" %% "xgettext" % "1.0")

scalacOptions += "-P:xgettext:xitrum.I18n"

// Template engine for Xitrum --------------------------------------------------

libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "1.9"

// Precompile Scalate
seq(scalateSettings:_*)

scalateTemplateConfig in Compile := Seq(TemplateConfig(
  file("src") / "main" / "scalate",  // See config/scalate.conf
  Seq(),
  Seq(Binding("helper", "xitrum.Action", true))
))

// Put config directory in classpath for easier development --------------------

// For "sbt console"
unmanagedClasspath in Compile <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

// For "sbt run"
unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "config") }

addCommandAlias("stage", ";xitrum-package")

XitrumPackage.copy("config", "public", "script")
