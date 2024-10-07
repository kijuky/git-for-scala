package io.github.kijuky.git

import io.github.kijuky.git.GitRepoFixture.withGitRepo
import io.github.kijuky.git.Implicits._
import org.scalatest.funspec.AnyFunSpec

class ImplicitsRichRevCommitSpec extends AnyFunSpec {
  describe("#author") {
    it("should be able to get authorUserName") {
      withGitRepo(
        Seq(
          // Setup
          "git config --local user.name test",
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.name == "refs/heads/main").head
        val Some(sut) = git.repository.revCommit(ref): @unchecked

        // Exercise
        val actual = sut.authorIdent

        // Verify
        val authorUserName = actual.getName
        assert(authorUserName == "test")
      }
    }

    it("should be able to get authorEmailAddress") {
      withGitRepo(
        Seq(
          // Setup
          "git config --local user.email test@example.com",
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.name == "refs/heads/main").head
        val Some(sut) = git.repository.revCommit(ref): @unchecked

        // Exercise
        val actual = sut.authorEmailAddress

        // Verify
        assert(actual == "test@example.com")
      }
    }
  }

  describe("#commitTime") {
    it("should be able to get commitTime") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial",
          "touch file2",
          "git add file2",
          // https://qiita.com/yoichi22/items/b25d223b639621b834cb#%E6%96%B9%E6%B3%952-date%E3%82%AA%E3%83%97%E3%82%B7%E3%83%A7%E3%83%B3
          "git commit -m second --date 1609426800",
          "git rebase HEAD~ --committer-date-is-author-date"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.name == "refs/heads/main").head
        val Some(sut) = git.repository.revCommit(ref): @unchecked

        // Exercise
        val actual = sut.commitTime

        // Verify
        assert(actual == 1609426800)
      }
    }

    it("should be able to get commitTimeInstant") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial",
          "touch file2",
          "git add file2",
          "git commit -m second --date 1609426800",
          "git rebase HEAD~ --committer-date-is-author-date"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.name == "refs/heads/main").head
        val Some(sut) = git.repository.revCommit(ref): @unchecked

        // Exercise
        val actual = sut.commitTimeInstant

        // Verify
        assert(actual.getEpochSecond == 1609426800)
      }
    }
  }

  describe("#commitMessage") {
    it("should be able to get commitMessage") {
      withGitRepo(
        Seq(
          // Setup
          "touch file",
          "git add file",
          "git commit -m initial"
        )
      ) { implicit git =>
        val ref = git.allBranches.filter(_.name == "refs/heads/main").head
        val Some(sut) = git.repository.revCommit(ref): @unchecked

        // Exercise
        val actual = sut.fullMessage

        // Verify
        assert(actual == "initial\n")
      }
    }
  }
}
