import uk.gov.hmrc.DefaultBuildSettings._

lazy val appName = "national-insurance-des-stub"

def unitFilter(name: String): Boolean   = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(PlayScala, SbtDistributablesPlugin)
  .settings(defaultSettings(): _*)
  .settings(
    scalaVersion := "2.13.10",
    retrieveManaged := true,
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
    Test / javaOptions += "-Dconfig.resource=test.application.conf",
    Test / testOptions := Seq(Tests.Filter(unitFilter)),
    addTestReportOption(Test, "test-reports")
  )
  .configs(IntegrationTest)
  .settings(
    inConfig(IntegrationTest)(Defaults.itSettings),
    integrationTestSettings(),
    IntegrationTest / testOptions := Seq(Tests.Filter(itTestFilter)),
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "test")).value,
    addTestReportOption(IntegrationTest, "int-test-reports")
  )
  .settings(
    coverageMinimumStmtTotal := 100,
    coverageFailOnMinimum := true,
    coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"
  )
  .settings(
    scalacOptions ++= Seq(
      "-Wconf:src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=views/.*:s"
    )
  )
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt Test/scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle Test/scalastyle")
