enablePlugins(GitVersioning)

name := "properly"

organization := "eu.timepit"
startYear := Some(2015)
licenses += "Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard"
)

scalacOptions in (Compile, doc) ++= Seq(
  "-diagrams",
  "-doc-source-url", scmInfo.value.get.browseUrl + "/tree/master€{FILE_PATH}.scala",
  "-sourcepath", baseDirectory.in(LocalRootProject).value.getAbsolutePath
)

autoAPIMappings := true

val catsFreeVersion = "2.0.0"
val catsEffectVersion = "2.0.0"
val scalaCheckVersion = "1.14.2"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.typelevel" %% "cats-free" % catsFreeVersion,
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test
)

scmInfo := Some(ScmInfo(url("https://github.com/fthomas/properly"),
  "git@github.com:fthomas/properly.git"))

initialCommands := """
  import eu.timepit.properly._
"""

publishMavenStyle := true

git.useGitDescribe := true

addCommandAlias("validate", Seq(
  "clean",
  "coverage",
  "test",
  "coverageReport",
  "scalastyle",
  "test:scalastyle",
  "doc",
  "packageSrc",
  "package"
).mkString(";", ";", ""))
