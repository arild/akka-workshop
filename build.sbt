name := "Akka workshop"

scalaVersion := "2.11.2"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.2"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.5"

libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.11" % "2.3.5"

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5"

testOptions in Test += Tests.Argument("-oD")
