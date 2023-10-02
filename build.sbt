import uk.gov.hmrc.DefaultBuildSettings.*

lazy val appName = "national-insurance-des-stub"

def unitFilter(name: String): Boolean   = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(
    scalaVersion := "2.13.12",
    majorVersion := 0,
    libraryDependencies ++= AppDependencies(),
    // To resolve a bug with version 2.x.x of the scoverage plugin - https://github.com/sbt/sbt/issues/6997
    libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always,
    routesImport += "uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders._",
    PlayKeys.playDefaultPort := 9688
  )
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
  .settings(
    Test / fork := true,
    Test / testOptions := Seq(Tests.Filter(unitFilter)),
    addTestReportOption(Test, "test-reports")
  )
  .configs(IntegrationTest)
  .settings(
    integrationTestSettings(),
    IntegrationTest / testOptions := Seq(Tests.Filter(itTestFilter)),
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "test")).value,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
  .settings(
    coverageMinimumStmtTotal := 100,
    coverageFailOnMinimum := true,
    coverageExcludedPackages := "<empty>;.*(Routes|definition).*"
  )
  .settings(
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    )
  )
  .disablePlugins(JUnitXmlReportPlugin)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt IntegrationTest/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle IntegrationTest/scalastyle")
