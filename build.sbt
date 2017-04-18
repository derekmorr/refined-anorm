name := "refined-anorm"

organization := "com.github.derekmorr"

version := "0.1"

lazy val commonSettings = Seq(
  scalaVersion := "2.12.1",
  crossScalaVersions := Seq("2.11.8", "2.12.1")
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

    "com.typesafe"        %  "config"         % "1.3.1"       % "it",
    "com.zaxxer"          %  "HikariCP"       % "2.4.1"       % "it",
    "org.postgresql"      %  "postgresql"     % "42.0.0"      % "it"
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

publishMavenStyle := false

publishArtifact in Test := false

publishTo := Some(Resolver.file("file",  new File(Path.userHome.absolutePath+"/.m2/repository")))

pomExtra :=
  <licenses>
    <license>
      <name>MIT License</name>
      <url>http://www.opensource.org/licenses/mit-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:derekmorr/refined-anorm.git</url>
    <connection>scm:git:git@github.com:derekmorr/refined-anorm.git</connection>
  </scm>
    <developers>
      <developer>
        <id>derekmorr</id>
        <name>Derek Morr</name>
        <url>https://github.com/derekmorr</url>
      </developer>
    </developers>
