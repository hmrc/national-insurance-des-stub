import play.sbt.PlayImport.*
import sbt.*

object AppDependencies {
  private lazy val bootstrapPlayVersion = "8.4.0"
  private lazy val hmrcMongoPlayVersion = "1.7.0"

  private lazy val compile: Seq[ModuleID] =
    Seq(
      ws,
      "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
      "uk.gov.hmrc"                  %% "domain-play-30"            % "9.0.0",
      "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"        % hmrcMongoPlayVersion,
      "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.16.1"
    )

  private val test: Seq[ModuleID]   =
    Seq(
      "org.scalatest" %% "scalatest"               % "3.2.17",
      "uk.gov.hmrc"   %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
      "org.mockito"   %% "mockito-scala-scalatest" % "1.17.30"
    ).map(_ % Test)

  // only add additional dependencies here - it test inherit test dependencies above already
  val itDependencies: Seq[ModuleID] =
    Seq(
      "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoPlayVersion
    )

  def apply(): Seq[ModuleID] = compile ++ test

}
