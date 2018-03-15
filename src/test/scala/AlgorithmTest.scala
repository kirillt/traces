import java.time.LocalDateTime

import Trace.Meeting
import org.scalatest.FunSpec

class AlgorithmTest extends FunSpec {

  val uidA = "kolya"
  val uidB = "petya"

  val configurations = Map(
    "strong" -> Algorithm(1.0, 10, 1),
    "weak" -> Algorithm(1.0, 0, 1))

  it("should detect meeting when people on certain distance for certain duration on the same floor") {
    val ts1 = LocalDateTime.now()
    val ts2 = ts1.plusSeconds(10)
    val ts3 = ts2.plusSeconds(10)

    val cases = Seq(
      Seq(Trace(ts1, 0.1, 0.2, 3, uidA),
        Trace(ts1, 0.3, 0.4, 3, uidB),
        Trace(ts2, 0.1, 0.2, 3, uidA),
        Trace(ts2, 0.3, 0.4, 3, uidB),
        Trace(ts3, +1, +2, 3, uidB)),

      Seq(Trace(ts1, 0.1, 0.2, 3, uidA),
        Trace(ts1, 0.3, 0.4, 3, uidB),
        Trace(ts2, 0.1, 0.2, 3, uidA),
        Trace(ts2, 0.3, 0.4, 3, uidB),
        Trace(ts3, -1, -2, 3, uidA)))

    val expected = List(Meeting(ts1, ts2))

    for ((label, algorithm) <- configurations) {
      println(s"Configuration: $label")
      assert(cases.map {
        traces => algorithm.detect(uidA, uidB)(traces.iterator).toList
      } == Seq(expected, expected))
    }
  }

  it("should detect 0-long meeting if one person just walks near") {
    val ts1 = LocalDateTime.now()
    val ts2 = ts1.plusSeconds(10)

    val cases = Seq(
      Seq(Trace(ts1, 0.1, 0.2, 3, uidA),
        Trace(ts1, 0.3, 0.4, 3, uidB),
        Trace(ts2, 0.1, 0.2, 3, uidA),
        Trace(ts2, +1, +2, 3, uidB)),

      Seq(Trace(ts1, 0.1, 0.2, 3, uidA),
        Trace(ts1, 0.3, 0.4, 3, uidB),
        Trace(ts2, 0.3, 0.4, 3, uidB),
        Trace(ts2, -1, -2, 3, uidA)))

    val expected = List(Meeting(ts1, ts1))

    assert(cases.map {
      traces => configurations("weak").detect(uidA, uidB)(traces.iterator).toList
    } == Seq(expected, expected))

    assert(cases.map {
      traces => configurations("strong").detect(uidA, uidB)(traces.iterator).toList
    } == Seq(List(), List()))
  }

}
