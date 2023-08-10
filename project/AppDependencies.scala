import play.core.PlayVersion
import sbt._

object AppDependencies {
  private lazy val bootstrapPlayVersion = "7.21.0"
  private lazy val hmrcMongoPlayVersion = "1.3.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "domain"                    % "8.3.0-play-28",
    "uk.gov.hmrc"                  %% "play-hmrc-api"             % "7.2.0-play-28",
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"        % hmrcMongoPlayVersion,
    "org.scalaj"                   %% "scalaj-http"               % "2.4.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.15.2"
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-test-play-28" % hmrcMongoPlayVersion,
    "org.scalatest"       %% "scalatest"               % "3.2.16",
    "uk.gov.hmrc"         %% "bootstrap-test-play-28"  % bootstrapPlayVersion,
    "org.mockito"         %% "mockito-scala-scalatest" % "1.17.14",
    "com.typesafe.play"   %% "play-test"               % PlayVersion.current,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.64.8"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID]      = compile ++ test

}
