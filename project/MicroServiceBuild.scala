import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt._

object MicroServiceBuild extends Build with MicroService {

  val appName = "national-insurance-des-stub"

  override lazy val appDependencies: Seq[ModuleID] = compile ++ test()

  val compile = Seq(
    ws,
    "uk.gov.hmrc" %% "microservice-bootstrap" % "5.15.0",
    "uk.gov.hmrc" %% "play-hmrc-api" % "1.4.0",
    "uk.gov.hmrc" %% "play-authorisation" % "4.3.0",
    "uk.gov.hmrc" %% "play-health" % "2.1.0",
    "uk.gov.hmrc" %% "play-ui" % "7.2.1",
    "uk.gov.hmrc" %% "play-config" % "4.3.0",
    "uk.gov.hmrc" %% "play-reactivemongo" % "5.2.0",
    "uk.gov.hmrc" %% "logback-json-logger" % "3.1.0",
    "uk.gov.hmrc" %% "domain" % "4.1.0"
  )

  def test(scope: String = "test,it") = Seq(
    "uk.gov.hmrc" %% "hmrctest" % "2.3.0" % scope,
    "uk.gov.hmrc" %% "reactivemongo-test" % "2.0.0" % scope,
    "org.scalatest" %% "scalatest" % "2.2.6" % scope,
    "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % scope,
    "org.mockito" % "mockito-core" % "2.8.9" % scope,
    "org.pegdown" % "pegdown" % "1.6.0" % scope,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
    "org.scalaj" %% "scalaj-http" % "1.1.6" % scope,
    "com.github.tomakehurst" % "wiremock" % "1.57" % scope
  )

}
