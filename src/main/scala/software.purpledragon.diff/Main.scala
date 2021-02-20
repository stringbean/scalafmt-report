package software.purpledragon.diff

import java.io.File
import java.nio.file.Paths

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

  errors.invalidFiles foreach { file =>
    file.failures foreach { line =>
      Console.println(s"::error file=${file.filename},line=$line::Incorrectly formatted line(s)")
    }
  }
}
