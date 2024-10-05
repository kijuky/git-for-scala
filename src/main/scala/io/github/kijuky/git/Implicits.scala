package io.github.kijuky.git

import org.eclipse.jgit.api.{Git, ListBranchCommand}
import org.eclipse.jgit.diff.{DiffEntry, DiffFormatter}
import org.eclipse.jgit.errors.InvalidObjectIdException
import org.eclipse.jgit.lib.{ObjectId, PersonIdent, Ref, Repository}
import org.eclipse.jgit.revwalk.{RevCommit, RevWalk}
import org.eclipse.jgit.revwalk.filter.RevFilter

import java.time.Instant
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import scala.util.control.Exception.ultimately

object Implicits {
  implicit class RichGit(git: Git) {
    def repository: Repository = git.getRepository

    def remoteBranches: Seq[Ref] =
      branches(ListBranchCommand.ListMode.REMOTE)
    def allBranches: Seq[Ref] =
      branches(ListBranchCommand.ListMode.ALL)
    private def branches(listMode: ListBranchCommand.ListMode): Seq[Ref] =
      git.branchList
        .setListMode(listMode)
        .call()
        .asScala
        .toSeq

    def tags: Seq[Ref] =
      git.tagList
        .call()
        .asScala
        .toSeq

    def commits(
      from: String,
      until: String,
      revFilter: RevFilter = RevFilter.ALL
    ): Seq[RevCommit] =
      (getObjectId(from), getObjectId(until)) match {
        case (Failure(e), _) =>
          throw new IllegalArgumentException(s"Ref not found: $from", e)
        case (_, Failure(e)) =>
          throw new IllegalArgumentException(s"Ref not found: $until", e)
        case (Success(sinceId), Success(untilId)) =>
          git.log
            .addRange(sinceId, untilId)
            .setRevFilter(revFilter)
            .call()
            .asScala
            .toSeq
      }

    def unstagedDiffs: Seq[DiffEntry] = git.diff.call().asScala.toSeq
    def diffs(from: String, until: String): Seq[DiffEntry] =
      (getObjectId(from), getObjectId(until)) match {
        case (Failure(e), _) =>
          throw new IllegalArgumentException(s"Ref not found: $from", e)
        case (_, Failure(e)) =>
          throw new IllegalArgumentException(s"Ref not found: $until", e)
        case (Success(sinceId), Success(untilId)) =>
          val diffFormatter = new DiffFormatter(System.out)
          diffFormatter.setRepository(repository)
          diffFormatter.scan(sinceId, untilId).asScala.toSeq
      }

    private def getObjectId(str: String): Try[ObjectId] =
      Option(repository.findRef(str)) match {
        case Some(ref) =>
          ref.objectId
            .toRight(new InvalidObjectIdException(str))
            .toTry
        case None =>
          Try(ObjectId.fromString(str))
      }
  }

  implicit class RichRepository(repo: Repository) {
    def revCommit(ref: Ref): Option[RevCommit] =
      ref.objectId.map(objectId => revWalk(_.parseCommit(objectId)))
    private def revWalk[T](openF: RevWalk => T): T =
      ultimately(repo.close())(openF(new RevWalk(repo)))
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
    def objectId: Option[ObjectId] = Option(ref.getObjectId)
  }

  implicit class RichDiffEntry(diffEntry: DiffEntry) {
    def oldPath: String = diffEntry.getOldPath
    def newPath: String = diffEntry.getNewPath
  }
}
