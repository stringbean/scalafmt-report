name := "scalafmt-report"

version := "1.0.0"
scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
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