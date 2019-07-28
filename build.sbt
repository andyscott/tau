lazy val core = (project in file(".core"))
  .settings(
    unmanagedSources in Compile := List(
      (baseDirectory in ThisBuild).value / "src" / "io" / "higherkindness" / "tau" / "tau.scala"
    )
  )
