package io.github.kijuky.git

import io.github.kijuky.git.GitRepoFixture.withGitRepo
import io.github.kijuky.git.Implicits._
import org.scalatest.funspec.AnyFunSpec

class ImplicitsRichGitSpec extends AnyFunSpec {
  describe("#branch") {
    it("should be able to get branches") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { sut =>
        // Exercise
        val actual = sut.allBranches

        // Verify
        val allBranchNames = actual.map(_.name)
        assert(allBranchNames == Seq("refs/heads/main"))
      }
    }
  }

  describe("#tags") {
    it("should be able to get tags") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial",
          "git tag tag"
        )
      ) { sut =>
        // Exercise
        val actual = sut.tags

        // Verify
        val tagNames = actual.map(_.name)
        assert(tagNames == Seq("refs/tags/tag"))
      }
    }
  }

  describe("#commits") {
    it("should be able to get commits") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial",
          "git branch feature",
          "git switch feature",
          "touch file2",
          "git add file2",
          "git commit -m second"
        )
      ) { sut =>
        // Exercise
        val actual = sut.commits("main", "HEAD")

        // Verify
        val commitMessages = actual.map(_.fullMessage)
        assert(commitMessages == Seq("second\n"))
      }
    }
  }

  describe("#diffs") {
    it("should be able to get unstaged diffs") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "touch file2",
          "git add file"
        )
      ) { sut =>
        // Exercise
        val actual = sut.unstagedDiffs

        // Verify
        val diffNames = actual.map(_.newPath)
        assert(diffNames == Seq("file2"))
      }
    }

    it("should be able t get diffs") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial",
          "git branch feature",
          "git switch feature",
          "touch file2",
          "git add file2",
          "git commit -m second"
        )
      ) { sut =>
        // Exercise
        val actual = sut.diffs("main", "HEAD")

        // Verify
        val diffNames = actual.map(_.newPath)
        assert(diffNames == Seq("file2"))
      }
    }
  }
}
