package io.github.kijuky.git

import org.eclipse.jgit.api.Git

import java.nio.file.{Files, Path}
import scala.language.postfixOps
import scala.sys.process._
import scala.util.control.Exception.ultimately

object GitRepoFixture {
  def withGitRepo(commands: Seq[String])(testCode: Git => Any): Unit = {
    val targetPath = Path.of("target")
    val repoPath = Files.createTempDirectory(targetPath, "test-repository-")
    val repoPathFile = repoPath.toFile
    val repoPathName = repoPathFile.getAbsolutePath
    s"mkdir -p $repoPathName" !;
    ultimately(s"rm -r $repoPathName" !) {
      for (
        command <- Seq(
          "git config --local init.defaultBranch main",
          "git config --local user.email you@example.com",
          "git config --local user.name YourName",
          "git init"
        ) ++ commands
      ) {
        // ! ではなく lineStream_! を使うと、成功する。
        val logs = Process(command, repoPathFile).lineStream_!.mkString("\n")
        // println(logs) // debug
      }
      val git = Git.open(repoPathFile)
      testCode(git)
    }
  }
}
