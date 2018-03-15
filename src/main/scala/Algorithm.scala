import java.time.temporal.ChronoUnit
import java.time.{Duration, LocalDateTime}

import Distance._
import Trace._

/**
 * @param DistanceThreshold consider people near if this threshold is passed, in meters
 * @param DurationThreshold consider meeting occurred if this threshold is passed, in seconds
 * @param TimeEpsilon consider two timestamps equal if the difference less than epsilon, in seconds
 */
case class Algorithm(DistanceThreshold: Double, DurationThreshold: Long, TimeEpsilon: Long) {

  type Track = Seq[T]

  case class State(a: Track, b: Track)

  def update(track: Track)(ts: LocalDateTime, place: Place): Track =
    (track :+ T(ts, place)).takeRight(2)

  def inEpsilon(ta: LocalDateTime, tb: LocalDateTime): Boolean =
    Duration.between(ta, tb).compareTo(epsilon) <= 0

  def determine(a: T, b: T): Distance =
    if (inEpsilon(a.timestamp, b.timestamp)) {
      Distance.determine(a.place, b.place)
    } else NotDetermined

  def neighbours(a: T, b: T): Boolean =
    determine(a, b).pass(DistanceThreshold)

  def detect(uidA: UID, uidB: UID)(traces: TraversableOnce[Trace]): Iterator[Meeting] = {
    val raw = traces.toIterator.scanLeft(State(Seq(), Seq())) {
      case (State(a, b), Trace(uid, T(ts, place))) =>
        if (uid == uidA) State(update(a)(ts, place), b) else
        if (uid == uidB) State(a, update(b)(ts, place)) else
          throw new IllegalStateException("Unselected UID encountered")
    } map {
      case State(a, b) if a.length < 2 || b.length < 2 => None
      case State(Seq(a1, a2), Seq(b1, b2)) =>
        lazy val from = min(a1.timestamp, b1.timestamp)
        lazy val to   = max(a2.timestamp, b2.timestamp)
        (neighbours(a1, b1), neighbours(a2, b2)) match {
          case (true, true) => Some(Meeting(from, to))
          case (true, _) => Some(Meeting(from, max(a1.timestamp, b1.timestamp)))
          case (_, true) => Some(Meeting(min(a2.timestamp, b2.timestamp), to))
          case _ => None
        }
    }

    Merge.overlaps {
      Merge.betweenSeparators(raw) {
        case (Meeting(from1, _), Meeting(_, to2)) => Meeting(from1, to2)
      } } {
        case (m1,m2) => m1.combineIfOverlaps(m2)
    } filter {
      _.pass(DurationThreshold)
    }
  }

  private val epsilon: Duration = Duration.of(TimeEpsilon, ChronoUnit.SECONDS)

  private def max(ts1: LocalDateTime, ts2: LocalDateTime): LocalDateTime =
    if (ts1.compareTo(ts2) > 0) ts1 else ts2

  private def min(ts1: LocalDateTime, ts2: LocalDateTime): LocalDateTime =
    if (ts1.compareTo(ts2) < 0) ts1 else ts2

}