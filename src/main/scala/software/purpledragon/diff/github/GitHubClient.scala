package software.purpledragon.diff.github

import org.json4s.{native, _}
import sttp.client3._
import sttp.client3.json4s._
import sttp.model.{HeaderNames, Uri}

import scala.util.{Failure, Success, Try}

object GitHubClient {
  private val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  private implicit val serialization: Serialization = native.Serialization
  private implicit val formats: Formats = DefaultFormats

  private val NextPage = "<(\\S+)>; rel=\"next\"".r

  def listPrFiles(prUrl: String): Try[Seq[String]] = {
    sys.env.get("GITHUB_TOKEN") match {
      case Some(token) =>
        paginateRequest(uri"$prUrl/files", token) map { files =>
          files collect {
            case f if f.status == "added" || f.status == "modified" => f.filename
          }
        }

      case None =>
        // TODO handle
        Failure(new IllegalStateException("Missing GITHUB_TOKEN"))
    }
  }

  private def paginateRequest(uri: Uri, token: String, acc: Seq[GitHubPullFile] = Nil): Try[Seq[GitHubPullFile]] = {
    val response = basicRequest
      .get(uri)
      .auth
      .bearer(token)
      .response(asJson[Seq[GitHubPullFile]])
      .send(backend)

    response.body.toTry flatMap { files =>
      response.header(HeaderNames.Link) match {
        case Some(NextPage(nextUrl)) =>
          paginateRequest(Uri(nextUrl), token, acc :++ files)

        case _ =>
          Success(acc :++ files)
      }
    }
  }
}

// status:
// added
// modified
// removed
case class GitHubPullFile(filename: String, status: String)
