import java.io.File

import Trace.UID

import scala.io.Source

trait TraceApp extends App {

  val SourceFile: File = {
    val path = mandatory("SOURCE")
    val source = new File(path)
    if (!source.exists()) {
      throw new IllegalArgumentException(s"Specified source file $path doesn't exist!")
    }
    source
  }

  val FullGraph: Boolean = sys.env.isDefinedAt("FULL_GRAPH")
  val TaskFile: String = sys.env.getOrElse("TASK_FILE", "")

  val PersonA: String = mandatoryIfNot(FullGraph, "A")
  val PersonB: String = mandatoryIfNot(FullGraph, "B")

  def mandatory(variable: String): String = {
    sys.env.get(variable) match {
      case None =>
        throw new IllegalArgumentException(s"Please, specify $variable environment variable")
      case Some(value) =>
        value
    }
  }

  def mandatoryIfNot(condition: Boolean, target: String): String =
    if (!condition) mandatory(target)
    else ""

  def rows: Iterator[Array[String]] = {
    val it = Source.fromFile(SourceFile).getLines()
    it.next //skip header
    it.map(_.split(','))
  }

  def traces(personA: UID, personB: UID): Iterator[Trace] = {
    def relevant(uid: Trace.UID): Boolean =
      uid == personA || uid == personB

    rows.filter(array => relevant(array(4)))
      .map(Trace(_))
  }

  def persons: Iterator[UID] =
    rows.map(_(4))

}
