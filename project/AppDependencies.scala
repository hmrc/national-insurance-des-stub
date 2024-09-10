import sbt.*

object AppDependencies {
  private lazy val bootstrapPlayVersion = "9.0.0"
  private lazy val hmrcMongoPlayVersion = "2.1.0"

  private lazy val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                  %% "bootstrap-backend-play-30" % bootstrapPlayVersion,
    "uk.gov.hmrc"                  %% "domain-play-30"            % "10.0.0",
    "uk.gov.hmrc.mongo"            %% "hmrc-mongo-play-30"        % hmrcMongoPlayVersion,
    "com.fasterxml.jackson.module" %% "jackson-module-scala"      % "2.17.2"
  )

  private val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapPlayVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoPlayVersion
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test

}
