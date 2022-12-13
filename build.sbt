import _root_.play.sbt.routes.RoutesKeys.routesImport
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

lazy val appName = "national-insurance-des-stub"

def unitFilter(name: String): Boolean   = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

defaultSettings()

lazy val microservice = (project in file("."))
  .settings(Compile / unmanagedResourceDirectories += baseDirectory.value / "resources")
  .settings(Test / testOptions := Seq(Tests.Filter(unitFilter), Tests.Argument("-eT")))
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(integrationTestSettings(): _*)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)

name := appName
scalaSettings
majorVersion := 0
scalaVersion := "2.12.16"
publishingSettings
retrieveManaged := true
routesImport += "uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders._"
resolvers += Resolver.jcenterRepo

PlayKeys.playDefaultPort := 9688
update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)

Test / javaOptions += "-Dconfig.resource=test.application.conf"
Test / fork := true
IntegrationTest / testOptions := Seq(Tests.Filter(itTestFilter), Tests.Argument("-eT"))
IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory)(base => Seq(base / "test")).value

libraryDependencies ++= AppDependencies()

// Coverage configuration
coverageMinimumStmtTotal := 95
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"

scalacOptions ++= Seq(
  "-Wconf:src=routes/.*:s",
  "-Wconf:cat=unused-imports&src=views/.*:s"
)

Global / excludeLintKeys += update / evictionWarningOptions

addCommandAlias("scalafmtAll", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("scalastyleAll", "all scalastyle test:scalastyle")
