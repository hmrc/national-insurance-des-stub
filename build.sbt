import uk.gov.hmrc.DefaultBuildSettings.*

lazy val appName = "national-insurance-des-stub"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.4.2"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(CodeCoverageSettings.settings)
  .settings(
    libraryDependencies ++= AppDependencies(),
    routesImport += "uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders._",
    PlayKeys.playDefaultPort := 9688
  )
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
  .settings(
    scalacOptions := scalacOptions.value.diff(Seq("-Wunused:all"))
  )
  .disablePlugins(JUnitXmlReportPlugin)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(itSettings())

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt it/Test/scalafmt")
