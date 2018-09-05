// Run sbt/sbt eclipse to create Eclipse project file
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "5.2.4")

// Run sbt/sbt xitrum-package to prepare for deploying to production environment
addSbtPlugin("tv.cntt" % "xitrum-package" % "1.9")

// For compiling Scalate templates in the compile phase of SBT
addSbtPlugin("org.scalatra.scalate" % "sbt-scalate-precompiler" % "1.9.0.0")
