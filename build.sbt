name := "lifoqueue-jqwik"

version := "0.1"

compile / javacOptions += "-Xlint:all"

javaOptions += "-enableassertions"

ThisBuild / libraryDependencies ++= Seq(
  "net.aichler" % "jupiter-interface" % "0.11.1" % Test,
  "net.jqwik"   % "jqwik"             % "1.8.2"  % Test
)

Test / parallelExecution := false

enablePlugins(JavaAppPackaging)
