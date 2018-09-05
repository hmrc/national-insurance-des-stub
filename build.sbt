import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Tests.{SubProcess, Group}
import play.sbt.routes.RoutesKeys.routesImport
import play.routes.compiler.StaticRoutesGenerator
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin._
import uk.gov.hmrc._
import DefaultBuildSettings._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning
import _root_.play.sbt.routes.RoutesKeys.routesGenerator

lazy val appName = "national-insurance-des-stub"
lazy val appDependencies: Seq[ModuleID] = compile ++ test

lazy val compile = Seq(
  ws,
  "uk.gov.hmrc" %% "microservice-bootstrap" % "8.2.0",
  "uk.gov.hmrc" %% "play-hmrc-api" % "2.0.0",
  "uk.gov.hmrc" %% "play-reactivemongo" % "6.2.0",
  "uk.gov.hmrc" %% "domain" % "5.2.0"
)

lazy val scope: String = "test,it"

def test = Seq(
  "uk.gov.hmrc" %% "hmrctest" % "3.0.0" % scope,
  "uk.gov.hmrc" %% "reactivemongo-test" % "3.1.0" % scope,
  "org.scalatest" %% "scalatest" % "3.0.1" % scope,
  "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
  "org.mockito" % "mockito-core" % "2.10.0" % scope,
  "org.pegdown" % "pegdown" % "1.6.0" % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.scalaj" %% "scalaj-http" % "2.3.0" % scope,
  "com.github.tomakehurst" % "wiremock" % "2.8.0" % scope
)

lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

def unitFilter(name: String): Boolean = name startsWith "unit"
def itTestFilter(name: String): Boolean = name startsWith "it"

lazy val microservice = (project in file("."))
  .enablePlugins(Seq(_root_.play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin, SbtArtifactory) ++ plugins: _*)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(publishingSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(
    name := appName,
    scalaVersion := "2.11.11",
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    evictionWarningOptions in update := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    routesGenerator := StaticRoutesGenerator,
    majorVersion := 0
  )
  .settings(routesImport += "uk.gov.hmrc.nationalinsurancedesstub.controllers.Binders._")
  .settings(
    unmanagedResourceDirectories in Compile += baseDirectory.value / "resources"
  )
  .settings(testOptions in Test := Seq(Tests.Filter(unitFilter)),
    addTestReportOption(Test, "test-reports")
  )
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    Keys.fork in IntegrationTest := false,
    testOptions in IntegrationTest := Seq(Tests.Filter(itTestFilter)),
    unmanagedSourceDirectories in IntegrationTest <<= (baseDirectory in IntegrationTest) (base => Seq(base / "test")),
    addTestReportOption(IntegrationTest, "int-test-reports"),
    testGrouping in IntegrationTest := oneForkedJvmPerTest((definedTests in IntegrationTest).value),
    parallelExecution in IntegrationTest := false)
  .settings(resolvers ++= Seq(
    Resolver.bintrayRepo("hmrc", "releases"),
    Resolver.jcenterRepo
  ))

def oneForkedJvmPerTest(tests: Seq[TestDefinition]) =
  tests map {
    test => Group(test.name, Seq(test), SubProcess(ForkOptions(runJVMOptions = Seq("-Dtest.name=" + test.name))))
  }

// Coverage configuration
coverageMinimum := 90
coverageFailOnMinimum := true
coverageExcludedPackages := "<empty>;com.kenshoo.play.metrics.*;.*definition.*;prod.*;testOnlyDoNotUseInAppConf.*;app.*;uk.gov.hmrc.BuildInfo"
