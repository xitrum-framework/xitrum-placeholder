organization := "takeharu.oshida"
name         := "xitrum-placeholder"
version      := "1.0-SNAPSHOT"

scalaVersion := "2.13.4"
scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

// Xitrum requires Java 8
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

//------------------------------------------------------------------------------

libraryDependencies += "tv.cntt" %% "xitrum" % "3.30.2"

// Xitrum uses SLF4J, an implementation of SLF4J is needed
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// For writing condition in logback.xml
libraryDependencies += "org.codehaus.janino" % "janino" % "3.1.2"

libraryDependencies += "com.newrelic.agent.java" % "newrelic-agent" % "6.3.0"

libraryDependencies += "com.martiansoftware" % "jsap" % "2.1"

// xgettext i18n translation key string extractor is a compiler plugin ---------

autoCompilerPlugins := true
addCompilerPlugin("tv.cntt" %% "xgettext" % "1.5.4")
scalacOptions += "-P:xgettext:xitrum.I18n"

// Scalate template engine config for Xitrum -----------------------------------

libraryDependencies += "tv.cntt" %% "xitrum-scalate" % "2.9.2"

// Precompile Scalate templates
import org.fusesource.scalate.ScalatePlugin._
scalateSettings
Compile / ScalateKeys.scalateTemplateConfig := Seq(TemplateConfig(
  (Compile / sourceDirectory).value / "scalate",
  Seq(),
  Seq(Binding("helper", "xitrum.Action", importMembers = true))
))

// Put config directory in classpath for easier development --------------------

// For "sbt console"
Compile / unmanagedClasspath += baseDirectory.value / "config"

// For "sbt fgRun"
Runtime / unmanagedClasspath += baseDirectory.value / "config"

XitrumPackage.copy("config", "public", "script")
addCommandAlias("stage", ";xitrumPackage")

fork := true
