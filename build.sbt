name := "TopHitsAnalysisOnTwitter"

version := "1.0"
//version := "0.1"

//scalaVersion := "2.12.4"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.2",
  "org.ccil.cowan.tagsoup" % "tagsoup" % "1.2.1",
  "org.twitter4j" % "twitter4j-stream" % "3.0.5",
  "oauth.signpost" % "signpost-core" % "1.2",
  "oauth.signpost" % "signpost-commonshttp4" % "1.2",
  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "org.apache.httpcomponents" % "httpcore" % "4.4.6",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.apache.spark" %% "spark-core" % "2.1.0",
  "org.apache.spark" %% "spark-mllib" % "2.1.0",
  "org.apache.spark" % "spark-streaming_2.11" % "2.1.0",
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.1.0").map(_.excludeAll(
  ExclusionRule(organization = "org.scalacheck"),
  ExclusionRule(organization = "org.scalactic"),
  ExclusionRule(organization = "org.scalatest")
))

val sprayGroup = "io.spray"
val sprayJsonVersion = "1.3.2"
libraryDependencies ++= List("spray-json") map { c => sprayGroup %% c % sprayJsonVersion }

libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0"
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0"classifier "models"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalamock" %% "scalamock-scalatest-support" % "3.5.0" % "test"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.16.0"
        
libraryDependencies += "edu.stanford.nlp" % "stanford-corenlp" % "3.3.1" artifacts (Artifact("stanford-corenlp", "models"), Artifact("stanford-corenlp"))
