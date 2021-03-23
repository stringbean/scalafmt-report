package software.purpledragon.diff

import software.purpledragon.diff.github.GitHubEventParser

import java.io.File
import java.nio.file.Paths
import scala.util.{Failure, Success}

object Main extends App {
  if (args.isEmpty) {
    Console.err.println("Missing input filename")
    System.exit(1)
  }

  val inputFile = new File(args.head)
  if (!inputFile.isFile) {
    Console.err.println(s"Cannot find input file ${args.head}")
    System.exit(1)
  }

  val basePath = Paths.get(args(1))

  val errors = FormatErrors(inputFile, basePath)

  // filter by PR

  val filteredFiles = sys.env.get("GITHUB_EVENT_PATH") match {
    case Some(eventPath) =>
      GitHubEventParser.parseAndExtractPrFiles(new File(eventPath)) match {
        case Success(prFiles) =>
          errors.invalidFiles filter { path =>
            prFiles.contains(path.filename)
          }

        case Failure(_) =>
          Console.println("::warning could not filter errors by PR")
          errors.invalidFiles
      }

    case None =>
      // don't filter
      errors.invalidFiles
  }

  filteredFiles foreach { file =>
    file.failures foreach { line =>
      Console.println(s"::error file=${file.filename},line=$line::Incorrectly formatted line(s)")
    }
  }
}
