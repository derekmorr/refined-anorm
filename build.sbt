name := "refined-anorm"

organization := "com.github.derekmorr"

version := "0.1"

lazy val commonSettings = Seq(
  scalaVersion := "2.12.2",
  crossScalaVersions := Seq("2.11.11", "2.12.2")
)

lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(commonSettings: _*).
  settings(Defaults.itSettings: _*)

libraryDependencies ++= {
  Seq(
    "com.typesafe.play"   %% "anorm"          % "2.5.3"       % Compile,
    "eu.timepit"          %% "refined"        % "0.8.0"       % Compile,

    "org.eu.acolyte"      %% "jdbc-scala"     % "1.0.43-j7p"  % Test,
    "org.scalacheck"      %% "scalacheck"     % "1.13.5"      % "it,test",
    "org.scalatest"       %% "scalatest"      % "3.0.1"       % "it,test",
    "org.pegdown"         %  "pegdown"        % "1.6.0"       % "it,test",

    // technically these dependencies are only needed in "it" scope, but publishLocal
    // will put them in compile scope unless they're tagged as "it,test"
    // see https://github.com/sbt/sbt/issues/1380
    "com.typesafe"        %  "config"         % "1.3.1"       % "it,test",
    "com.zaxxer"          %  "HikariCP"       % "2.4.1"       % "it,test",
    "org.postgresql"      %  "postgresql"     % "42.0.0"      % "it,test"
  )
}

scalacOptions ++= Seq(
  "-feature", "-unchecked", "-deprecation", "-Xcheckinit", "-Xlint",
  "-Xfatal-warnings", "-g:line", "-Ywarn-dead-code", "-Ywarn-numeric-widen")

// don't let Ctrl-C exit
cancelable in Global := true

autoAPIMappings := true

// generate HTML reports for tests
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/report")

// show test durations
(testOptions in Test) += Tests.Argument(TestFrameworks.ScalaTest, "-oD")

// run tests in parallel
parallelExecution in Test := true

// cache dependency resolution information
updateOptions := updateOptions.value.withCachedResolution(true)

publishMavenStyle := true

publishArtifact in Test := false

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.php"))

homepage := Some(url("https://github.com/derekmorr/refined-anorm/"))

scmInfo := Some(
  ScmInfo(
    url("http://github.com/derekmorr/refined-anorm/tree/master"),
    "scm:git@github.com:derekmorr/refined-anorm.git"
  )
)

developers := List(
  Developer(
    id    = "derekmorr",
    name  = "Derek Morr",
    email = "morr.derek@gmail.com",
    url   = url("https://github.com/derekmorr")
  )
)

