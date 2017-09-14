name := "AppAnalytics"

version := "1.0"

scalaVersion := "2.12.1"

scalacOptions in ThisBuild ++= Seq("-unchecked", "-deprecation", "-feature")


//command to run main class from terminal
//  1. sbt assembly
//  2. java -jar AppAnalytics-assembly-1.0.jar (./target/scala-2.12/AppAnalytics-assembly-1.0.jar)

mainClass in (Compile, assembly) := Some("com.rajeev.Main")