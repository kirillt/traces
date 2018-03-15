import org.scalatest.FunSpec

class MergeTest extends FunSpec {

  it("should merge all Some elements between None elements") {
    val merged = Merge.betweenSeparators(Seq(Some(1), Some(2), None,
      None, Some(5), None, Some(7), Some(8), Some(9)).iterator) {
      case (from,to) => (from, to)
    }.toList

    assert(merged == List((1,2),(5,5),(7,9)))
  }

  it("should merge chains of overlapped elements") {
    val merged = Merge.overlaps(Seq(1 to 3, 2 to 3, 4 to 5, 6 to 8, 7 to 9, 9 to 13, 12 to 17,
      Seq(18), Seq(19), 20 to 23).iterator) {
        case (l, r) => if (l.intersect(r).isEmpty) None
          else Some(l.union(r).toSet.toList.sorted)
    }.toList

    assert(merged == List(1 to 3, 4 to 5, 6 to 17, Seq(18), Seq(19), 20 to 23).map(_.toList))
  }

}
