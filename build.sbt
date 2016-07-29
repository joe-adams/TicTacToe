name := "tictactoe"

version := "1.0"

scalaVersion := "2.11.8"


libraryDependencies  ++= Seq(
  "org.scalafx" %% "scalafx" % "8.0.92-R10"  withSources() withJavadoc(),
  "com.netflix.rxjava" % "rxjava-scala" % "0.20.7"  withSources() withJavadoc(),
  "org.scalanlp" %% "breeze" % "0.12"   withSources() withJavadoc(),
  "com.typesafe.slick" %% "slick" % "3.1.1" withSources() withJavadoc(),
  "org.slf4j" % "slf4j-nop" % "1.7.10" withSources() withJavadoc(),
  "com.h2database" % "h2" % "1.4.187" withSources() withJavadoc(),
  "postgresql" % "postgresql" % "9.1-901.jdbc4" withSources() withJavadoc(),
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1" withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "2.2.4" % "test" withSources() withJavadoc()

)



resolvers ++= Seq(
  // other resolvers here
  // if you want to use snapshot builds (currently 0.13-0598e003cfa7f00f76919aa556009ad6d4fc1332-SNAPSHOT), use this.
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"
)



unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/ext/jfxrt.jar"))

mainClass in assembly := some("com.github.joeadams.TicTacToe")

assemblyJarName in assembly := "tictactoe.jar"

fork in run := true

