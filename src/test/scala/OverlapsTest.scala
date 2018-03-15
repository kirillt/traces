import java.time.LocalDateTime

import Trace.Meeting
import org.scalatest.FunSpec

class OverlapsTest extends FunSpec {

  it("should find overlap of two meetings") {
    val ts1 = LocalDateTime.now()
    val ts2 = ts1.plusHours(1)
    val ts3 = ts2.plusHours(1)
    val ts4 = ts3.plusHours(1)

    assert(Meeting(ts1, ts3).combineIfOverlaps(Meeting(ts2, ts4))
      .contains(Meeting(ts1, ts4)))
    assert(Meeting(ts1, ts4).combineIfOverlaps(Meeting(ts2, ts3))
      .contains(Meeting(ts1, ts4)))
    assert(Meeting(ts2, ts4).combineIfOverlaps(Meeting(ts1, ts3))
      .contains(Meeting(ts1, ts4)))
    assert(Meeting(ts2, ts3).combineIfOverlaps(Meeting(ts1, ts4))
      .contains(Meeting(ts1, ts4)))
    assert(Meeting(ts1, ts2).combineIfOverlaps(Meeting(ts2, ts3))
      .contains(Meeting(ts1, ts3)))
    assert(Meeting(ts2, ts3).combineIfOverlaps(Meeting(ts1, ts2))
      .contains(Meeting(ts1, ts3)))
    assert(Meeting(ts1, ts2).combineIfOverlaps(Meeting(ts3, ts4))
      .isEmpty)
    assert(Meeting(ts3, ts4).combineIfOverlaps(Meeting(ts1, ts2))
      .isEmpty)
  }

}
