name := "RecommendationGenSBT"

version := "1.0"

scalaVersion := "2.10.5"


mainClass in (Compile, run) := Some("RecommendationGenerator")

libraryDependencies ++= Seq(
  "org.apache.spark" % "spark-core_2.10" % "1.6.2" % "provided"
)

libraryDependencies += "org.apache.spark" % "spark-sql_2.10" % "1.4.0"
