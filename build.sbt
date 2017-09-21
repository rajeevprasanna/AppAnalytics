name := "AppAnalytics"

version := "1.0"

scalaVersion := "2.12.2"

// Disable parallel execution of tests
parallelExecution in Test := false

// Fork tests in case native LevelDB database is used
fork := true

libraryDependencies ++= {
  val akkaVersion       = "2.5.4"
  val sprayVersion      = "1.3.3"
  Seq(
    // Akka
    "com.typesafe.akka"           %% "akka-actor"       % akkaVersion,
    "com.typesafe.akka"           %% "akka-persistence" % akkaVersion,
    "com.typesafe.akka"           %% "akka-slf4j"       % akkaVersion,

    // Local journal (Akka Persistence)
    // http://doc.akka.io/docs/akka/2.4.1/scala/persistence.html#Local_LevelDB_journal
    "org.iq80.leveldb"            % "leveldb"          % "0.7",
    "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8",

    // Spray JSON for Serialization
    "io.spray"                    %%  "spray-json"     % sprayVersion,

    // Commons IO is needed for cleaning up data when testing persistent actors
    "commons-io"                  % "commons-io"       % "2.4",
    "ch.qos.logback"              %  "logback-classic"  % "1.1.3",

    "org.apache.commons" % "commons-compress" % "1.5",

    //Mailgun dependencies
    "com.sun.jersey" % "jersey-client" % "1.19.4",
    "com.sun.jersey" % "jersey-core" % "1.19.4",
    "com.sun.jersey.contribs" % "jersey-multipart" % "1.19.4",

    //File Utils
    "com.github.pathikrit" % "better-files_2.12" % "3.0.0"
  )
}


//mainClass in (Compile, assembly) := Some("com.rajeev.Main")