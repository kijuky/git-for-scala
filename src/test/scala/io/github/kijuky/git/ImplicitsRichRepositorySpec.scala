package io.github.kijuky.git

import io.github.kijuky.git.GitRepoFixture.withGitRepo
import io.github.kijuky.git.Implicits._
import org.scalatest.funspec.AnyFunSpec

class ImplicitsRichRepositorySpec extends AnyFunSpec {
  describe("#revCommit") {
    it("should be able to get revCommit") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { git =>
        val ref = git.allBranches.filter(_.name == "refs/heads/main").head
        val sut = git.repository

        // Exercise
        val actual = sut.revCommit(ref)

        // Verify
        val Some(fullMessage) = actual.map(_.fullMessage): @unchecked
        assert(fullMessage == "initial\n")
      }
    }
  }
}
