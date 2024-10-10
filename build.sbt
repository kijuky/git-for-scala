lazy val root = project
  .in(file("."))
  .settings(
    name := "git-for-scala",
    scalaVersion := "2.12.20",
    crossScalaVersions := Seq(scalaVersion.value, "3.3.4"),
    libraryDependencies ++= Seq(
      "org.eclipse.jgit" % "org.eclipse.jgit" % "7.0.0.202409031743-r",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.slf4j" % "slf4j-simple" % "2.0.16" % Test
    )
  )

inThisBuild(
  Seq(
    organization := "io.github.kijuky",
    homepage := Some(url("https://github.com/kijuky/git-for-scala")),
    licenses := Seq(
      "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "kijuky",
        "Kizuki YASUE",
        "ikuzik@gmail.com",
        url("https://github.com/kijuky")
      )
    ),
    versionScheme := Some("early-semver"),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )
)
