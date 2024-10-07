package io.github.kijuky.git

import io.github.kijuky.git.GitRepoFixture.withGitRepo
import io.github.kijuky.git.Implicits._
import org.scalatest.funspec.AnyFunSpec

class ImplicitsRichRefSpec extends AnyFunSpec {
  describe("#name") {
    it("should be able to get name") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.getName == "refs/heads/main").head

        // Exercise
        val actual = ref.name

        // Verify
        assert(actual == "refs/heads/main")
      }
    }
  }

  describe("#objectId") {
    it("should be able to get objectId") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.getName == "refs/heads/main").head

        // Exercise
        val actual = ref.objectId

        // Verify
        assert(actual.isDefined)
      }
    }
  }
}
