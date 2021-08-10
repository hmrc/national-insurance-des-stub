import _root_.play.sbt.routes.RoutesKeys.routesImport
import play.core.PlayVersion
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

lazy val appName = "national-insurance-des-stub"
val silencerVersion = "1.7.1"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()

def unitFilter(name: String): Boolean = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

lazy val compile = Seq(
  "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.10.0",
  "uk.gov.hmrc" %% "domain" % "6.2.0-play-28",
  "uk.gov.hmrc" %% "play-hmrc-api" % "6.4.0-play-28",
  "uk.gov.hmrc" %% "simple-reactivemongo" % "8.0.0-play-28",
  compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
)

def test(scope: String = "test, it") = Seq(
  "uk.gov.hmrc" %% "reactivemongo-test" % "5.0.0-play-28" % scope,
  "org.scalatest" %% "scalatest" % "3.0.9" % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % scope,
  "org.mockito" % "mockito-core" % "2.28.2" % scope,
  "org.pegdown" % "pegdown" % "1.6.0" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalaj" %% "scalaj-http" % "2.4.2" % scope,
  "com.github.tomakehurst" % "wiremock-jre8" % "2.26.3" % scope
)

defaultSettings()

lazy val microservice = (project in file("."))
  .settings(unmanagedResourceDirectories in Compile += baseDirectory.value / "resources")
  .settings(testOptions in Test := Seq(Tests.Filter(unitFilter), Tests.Argument("-eT")))
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .disablePlugins(sbt.plugins.JUnitXmlReportPlugin)

enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)

name := appName
scalaSettings
majorVersion := 0
scalaVersion := "2.12.12"
publishingSettings
integrationTestSettings()
retrieveManaged := true
routesImport += "uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders._"
resolvers += Resolver.jcenterRepo

PlayKeys.playDefaultPort := 9688
evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false)

javaOptions in Test += "-Dconfig.resource=test.application.conf"
fork in Test := true
testOptions in IntegrationTest := Seq(Tests.Filter(itTestFilter), Tests.Argument("-eT"))
unmanagedSourceDirectories in IntegrationTest := (baseDirectory in IntegrationTest) (base => Seq(base / "test")).value

libraryDependencies ++= appDependencies

// Coverage configuration
coverageMinimum := 86
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"

scalacOptions ++= Seq(
  "-P:silencer:pathFilters=views;routes"
)
