scalaVersion := "2.11.12"

version := "0.1"

Compile / mainClass := Some("tech.igorramazanov.coinkeeper.fetcher.Main")

libraryDependencies ++= Seq(
  "com.softwaremill.sttp" %%% "core"     % "1.7.2",
  "io.argonaut"           %%% "argonaut" % "6.2.3"
)

Global / onChangedBuildSource := ReloadOnSourceChanges

nativeGC := "none"
nativeMode := "release"

enablePlugins(ScalaNativePlugin)
