name := "Akka workshop"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.10.3"

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.10" % "2.3.2"

libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.10" % "2.3.2"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.0-M1"

libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5"

testOptions in Test += Tests.Argument("-oD")
