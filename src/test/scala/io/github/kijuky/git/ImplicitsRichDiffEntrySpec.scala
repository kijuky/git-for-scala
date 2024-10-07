package io.github.kijuky.git

import io.github.kijuky.git.GitRepoFixture.withGitRepo
import io.github.kijuky.git.Implicits._
import org.scalatest.funspec.AnyFunSpec

class ImplicitsRichDiffEntrySpec extends AnyFunSpec {
  describe("#oldPath") {
    it("should be able to get oldPath") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial",
          "rm file"
        )
      ) { implicit git =>
        val sut = git.unstagedDiffs.head

        // Exercise
        val actual = sut.oldPath

        // Verify
        assert(actual == "file")
      }
    }
  }

  describe("#newPath") {
    it("should be able to get newPath") {
      withGitRepo(
        Seq(
          // Setup
          "touch file"
        )
      ) { implicit git =>
        val sut = git.unstagedDiffs.head

        // Exercise
        val actual = sut.newPath

        // Verify
        assert(actual == "file")
      }
    }
  }
}
