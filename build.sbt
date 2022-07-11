import _root_.play.sbt.routes.RoutesKeys.routesImport
import play.core.PlayVersion
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._

lazy val appName = "national-insurance-des-stub"

val silencerVersion = "1.7.9"
val bootstrapPlayVersion = "6.2.0"
val hmrcMongoPlayVersion = "0.66.0"

lazy val appDependencies: Seq[ModuleID] = compile ++ test()

def unitFilter(name: String): Boolean = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

lazy val compile = Seq(
  "uk.gov.hmrc"                  %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
  "uk.gov.hmrc"                  %% "domain"                    % "8.1.0-play-28",
  "uk.gov.hmrc"                  %% "play-hmrc-api"             % "7.0.0-play-28",
  "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"        % hmrcMongoPlayVersion,
  "org.scalaj"                   %% "scalaj-http"               % "2.4.2",
  "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.13.3",
  compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
)

def test(scope: String = "test, it"): Seq[ModuleID] = Seq(
  "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % hmrcMongoPlayVersion,
  "org.scalatest"          %% "scalatest"               % "3.2.12",
  "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % bootstrapPlayVersion,
  "org.scalatestplus"      %% "mockito-4-5"             % "3.2.12.0",
  "com.typesafe.play"      %% "play-test"               % PlayVersion.current,
  "com.vladsch.flexmark"   %  "flexmark-all"            % "0.62.2"
).map(_ % scope)

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
IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory) (base => Seq(base / "test")).value

libraryDependencies ++= appDependencies

// Coverage configuration
coverageMinimumStmtTotal := 95
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"

scalacOptions ++= Seq(
  "-P:silencer:pathFilters=views;routes"
)

Global / excludeLintKeys += update / evictionWarningOptions
