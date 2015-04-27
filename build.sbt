enablePlugins(GitVersioning)

name := "properly"

organization := "eu.timepit"
startYear := Some(2015)
licenses += "GPL-3.0" -> url("http://www.gnu.org/licenses/gpl-3.0.html")

scalaVersion := "2.11.6"
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

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1",
  "org.scalaz" %% "scalaz-concurrent" % "7.1.1",
  "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
)

scmInfo := Some(ScmInfo(url("https://github.com/fthomas/properly"),
  "git@github.com:fthomas/properly.git"))

initialCommands := """
  import eu.timepit.properly._
"""

bintraySettings

publishMavenStyle := true

scalariformSettings

git.useGitDescribe := true
