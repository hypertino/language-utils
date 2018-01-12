crossScalaVersions := Seq("2.12.4", "2.11.12")

scalaVersion := crossScalaVersions.value.head

lazy val `language-utils` = project in file(".") settings (
    name := "language-utils",
    version := "0.1.2",
    organization := "com.hypertino",  
    resolvers ++= Seq(
      Resolver.sonatypeRepo("public")
    ),
  libraryDependencies ++= Seq(
    "com.hypertino"         %% "binders"                            % "1.2.1",
    "com.google.guava"      % "guava"                               % "18.0",
    "org.scalamock"         %% "scalamock-scalatest-support"        % "3.5.0" % "test"
  )
)
