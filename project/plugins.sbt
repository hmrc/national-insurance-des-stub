resolvers += Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
resolvers += "HMRC Releases" at "https://dl.bintray.com/hmrc/releases"

addSbtPlugin("uk.gov.hmrc" % "sbt-settings" % "4.1.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "2.3.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "2.0.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-artifactory" % "1.0.0")
addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "1.6.0")
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.19")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

