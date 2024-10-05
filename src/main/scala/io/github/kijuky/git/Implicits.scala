package io.github.kijuky.git

import org.eclipse.jgit.api.{Git, ListBranchCommand}
import org.eclipse.jgit.diff.{DiffEntry, DiffFormatter}
import org.eclipse.jgit.lib.{ObjectId, PersonIdent, Ref, Repository}
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.revwalk.filter.RevFilter

import java.time.Instant
import scala.collection.JavaConverters._
import scala.util.control.Exception.ultimately

object Implicits {
  implicit class RichGit(git: Git) {
    private lazy val repo = git.getRepository

    def branches: Seq[Ref] =
      git.branchList
        .setListMode(ListBranchCommand.ListMode.REMOTE)
        .call()
        .asScala

    def tags: Seq[Ref] =
      git.tagList
        .call()
        .asScala

    def commits(
      from: String,
      until: String,
      revFilter: RevFilter = RevFilter.ALL
    ): Seq[RevCommit] = {
      val sinceId = repo.findRef(from).objectId
      val untilId = repo.findRef(until).objectId
      git.log
        .addRange(sinceId, untilId)
        .setRevFilter(revFilter)
        .call()
        .asScala
        .toSeq
    }

    def diffs: Seq[DiffEntry] = git.diff().call().asScala
    def diffs(a: String, b: String): Seq[DiffEntry] = {
      def getObjectId(a: String) =
        Option(repo.findRef(a))
          .map(_.objectId)
          .getOrElse(ObjectId.fromString(a))

      val aId = getObjectId(a)
      val bId = getObjectId(b)
      val diffFormatter = new DiffFormatter(System.out)
      diffFormatter.setRepository(repo)
      diffFormatter.scan(aId, bId).asScala
    }
  }

  implicit class RichRepository(repo: Repository) {
    def revWalk[T](openF: RevWalk => T): T =
      ultimately(repo.close())(openF(new RevWalk(repo)))

    def revCommit(ref: Ref): RevCommit =
      revWalk(_.parseCommit(ref.objectId))
  }

  implicit class RichRevCommit(revCommit: RevCommit) {
    def authorIdent: PersonIdent = revCommit.getAuthorIdent
    def authorEmailAddress: String = authorIdent.getEmailAddress
    def commitTime: Int = revCommit.getCommitTime
    def commitTimeInstant: Instant = Instant.ofEpochSecond(commitTime)
    def fullMessage: String = revCommit.getFullMessage
  }

  implicit class RichRef(ref: Ref) {
    def name: String = ref.getName
    def simpleName: String = name.replace("refs/remotes/origin/", "")
    def objectId: ObjectId = ref.getObjectId
  }

  implicit class RichDiffEntry(diffEntry: DiffEntry) {
    def oldPath: String = diffEntry.getOldPath
    def newPath: String = diffEntry.getNewPath
  }
}
