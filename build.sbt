name := "scalafmt-report"

version := "1.1.0-alpha"
scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.softwaremill.sttp.client3" %% "core" % "3.1.9",
  "com.softwaremill.sttp.client3" %% "json4s" % "3.1.9",
  "org.json4s" %% "json4s-native" % "3.6.11",
  "org.scalatest" %% "scalatest" % "3.2.3" % Test
)

enablePlugins(NativeImagePlugin)
nativeImageOptions ++= {
  sys.env
    .get("NATIVE_IMAGE_MUSL")
    .map(_ => s"--libc=musl")
    .toSeq ++
    sys.env
      .get("NATIVE_IMAGE_STATIC")
      .map(_.toBoolean)
      .filter(identity)
      .map(_ => "--static")
      .toSeq
}
