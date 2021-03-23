package software.purpledragon.diff.github

import org.json4s._
import org.json4s.native.JsonMethods._

import java.io.File
import scala.util.{Failure, Try}

object GitHubEventParser {
  private implicit val formats: Formats = DefaultFormats

  def parseAndExtractPrFiles(eventPayload: File): Try[Seq[String]] = {
    if (!eventPayload.isFile) {
      Failure(new IllegalStateException("Missing payload file"))
    }

    val event = parse(eventPayload).camelizeKeys.extract[GitHubEvent]

    event.pullRequest match {
      case Some(pr) =>
        GitHubClient.listPrFiles(pr.url)

      case None =>
        Failure(new IllegalStateException("PR not in payload"))
    }
  }
}

case class GitHubEvent(pullRequest: Option[GitHubPullRequest])
case class GitHubPullRequest(id: Long, url: String)
