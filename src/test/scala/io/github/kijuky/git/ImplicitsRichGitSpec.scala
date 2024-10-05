package io.github.kijuky.git

import io.github.kijuky.git.Implicits.RichGit
import org.eclipse.jgit.api.Git
import org.scalatest.funspec.AnyFunSpec

import java.io.File
import scala.language.postfixOps
import scala.util.control.Exception.ultimately
import scala.sys.process._

class ImplicitsRichGitSpec extends AnyFunSpec {
  def withRepository(testCode: Git => Any): Unit = {
    val dirName = "repo"
    s"mkdir $dirName" !;
    ultimately(s"rm -r $dirName" !) {
      s"git init $dirName -b main" !
      val git = Git.open(new File(dirName))
      testCode(git)
    }
  }

  describe("Implicits.RichGit") {
    it("should be able to get branches") {
      withRepository { git =>
        // When
        val branches = git.branches
        // Then
        assert(branches.nonEmpty)
      }
    }
  }
}
