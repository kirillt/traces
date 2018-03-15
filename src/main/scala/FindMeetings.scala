import scala.io.Source
import java.text.NumberFormat
import java.util.Locale

import Trace.{Meeting, UID}

object FindMeetings extends TraceApp {

  val Distance: Double = sys.env.getOrElse("DISTANCE", "1").toDouble
  val Duration: Int = sys.env.getOrElse("DURATION", "60").toInt
  val Epsilon: Int = sys.env.getOrElse("EPSILON", "7").toInt

  val OnlyFirst: Boolean = sys.env.isDefinedAt("ONLY_FIRST")
  val OnlyCount: Boolean = sys.env.isDefinedAt("ONLY_COUNT")

  println(s"Input variables:\n" +
    s"\tsource file: $SourceFile\n" +
    s"\tdistance threshold: $Distance meters\n" +
    s"\tduration threshold: $Duration seconds\n" +
    s"\ttime epsilon: $Epsilon seconds\n" +
    s"\tonly first: $OnlyFirst\n" +
    s"\tfull graph: $FullGraph\n" +
    s"\tuid A: $PersonA\n" +
    s"\tuid B: $PersonB\n")

  val algorithm = Algorithm(Distance, Duration, Epsilon)

  if (FullGraph) {
    val selected = if (TaskFile != "") {
        Source.fromFile(TaskFile).getLines()
      } else persons

    val uids = selected.toSet.toVector
    val n = uids.size
    println(s"Found $n uids in the source")

    for {i <- 0 until n; j <- i+1 until n} {
      val personA = uids(i)
      val personB = uids(j)
      print(s"$personA, $personB: ")
      runFor(verboseDefault = false)(personA, personB)
    }
  } else {
    println(s"This program will search for meetings of $PersonA and $PersonB.\n" +
      s"Two persons are considered met if they are on distance of $Distance meters for $Duration seconds.\n")
    runFor(verboseDefault = true)(PersonA, PersonB)
  }

  def runFor(verboseDefault: Boolean)(personA: UID, personB: UID): Unit = {
    val verbose = !OnlyCount && verboseDefault
    val start = System.nanoTime()

    val meetings: Iterator[Meeting] = algorithm.detect(personA, personB)(traces(personA, personB))
    if (meetings.hasNext) {
      val it = if (OnlyFirst) Seq(meetings.next) else meetings
      if (verbose) {
        for (meeting <- it) {
          println(s"Found meeting of $personA and $personB between ${meeting.from} to ${meeting.to}")
        }
        println()
      } else {
        print(s"${it.size} meetings, ")
      }
    } else {
      if (verbose) println(s"No meeting of $personA and $personB found.\n")
      else print("0 meetings, ")
    }

    val finish = System.nanoTime()
    val formatter = NumberFormat.getIntegerInstance(Locale.UK)
    val time = formatter.format(finish - start)
    if (verbose) println(s"Time = ${time}ns")
    else println(s"time = ${time}ns")
  }

}
