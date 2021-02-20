package software.purpledragon.diff

import software.purpledragon.diff.FormatErrors._

import java.io.File
import java.nio.file.{Path, Paths}
import scala.io.Source

object FormatErrors {
  private val FromFile = "^--- (.*)$".r
  private val ChangeBlock = "^@@ -([0-9]+),([0-9]+) \\+([0-9]+),([0-9]+) @@$".r

  def apply(in: File, basePath: Path): FormatErrors = new FormatErrors(Source.fromFile(in), basePath)
}

class FormatErrors(in: Source, basePath: Path) {
  val invalidFiles: Seq[InvalidFile] = in.getLines().foldLeft(List[InvalidFile]()) { (acc, line) =>
    line match {
      case FromFile(filename) =>
        // new file - add to list
        val relativePath = basePath.relativize(Paths.get(filename))
        InvalidFile(relativePath.toString, Nil) :: acc

      case ChangeBlock(startLine, _, _, _) =>
        acc.head.addLine(startLine.toInt) :: acc.tail

      case _ =>
        // not a block or file - ignore
        acc
    }
  } sortBy (_.filename)

}

case class InvalidFile(filename: String, failures: Seq[Int]) {
  def addLine(line: Int): InvalidFile = {
    copy(failures = failures :+ line)
  }
}
