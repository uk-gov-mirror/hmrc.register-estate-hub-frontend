import sbt.*

object AppDependencies {

  val boostrapVersion = "8.6.0"
  val mongoVersion = "2.2.0"

  private lazy val compile = Seq(
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-30"                     % mongoVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30"             % "9.11.0",
    "uk.gov.hmrc"             %% "domain-play-30"                         % "9.0.0",
    "uk.gov.hmrc"             %% "play-conditional-form-mapping-play-30"  % "2.0.0",
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30"             % boostrapVersion,
    "uk.gov.hmrc"             %% "tax-year"                               % "4.0.0"
  )

  private lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"                 %% "bootstrap-test-play-30"   % boostrapVersion,
    "uk.gov.hmrc.mongo"           %% "hmrc-mongo-test-play-30"  % mongoVersion,
    "org.scalatestplus"           %% "scalacheck-1-17"          % "3.2.18.0",
    "org.jsoup"                   %  "jsoup"                    % "1.18.1",
    "org.mockito"                 %% "mockito-scala-scalatest"  % "1.17.37",
    "wolfendale"                  %% "scalacheck-gen-regexp"    % "0.1.2"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
