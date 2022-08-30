import play.core.PlayVersion
import sbt._

object AppDependencies {
  private lazy val silencerVersion      = "1.7.9"
  private lazy val monocleVersion       = "1.7.3"
  private lazy val bootstrapPlayVersion = "7.1.0"
  private lazy val hmrcMongoPlayVersion = "0.71.0"

  lazy val compile = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-28" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "domain"                    % "8.1.0-play-28",
    "uk.gov.hmrc"                  %% "play-hmrc-api"             % "7.0.0-play-28",
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-28"        % hmrcMongoPlayVersion,
    "org.scalaj"                   %% "scalaj-http"               % "2.4.2",
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.13.3",
    compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
    "com.github.ghik"               % "silencer-lib"              % silencerVersion % Provided cross CrossVersion.full
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc.mongo"   %% "hmrc-mongo-test-play-28" % hmrcMongoPlayVersion,
    "org.scalatest"       %% "scalatest"               % "3.2.13",
    "uk.gov.hmrc"         %% "bootstrap-test-play-28"  % bootstrapPlayVersion,
    "org.scalatestplus"   %% "mockito-4-5"             % "3.2.12.0",
    "com.typesafe.play"   %% "play-test"               % PlayVersion.current,
    "com.vladsch.flexmark" % "flexmark-all"            % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID]      = compile ++ test

}
