import java.time.{Duration, LocalDateTime}
import java.time.format.DateTimeFormatter

import Trace._

case class T(timestamp: LocalDateTime, place: Place)

case class Trace(uid: UID, trace: T)

object Trace {

  type UID = String

  type Floor = Int

  case class Point(x: Double, y: Double)

  case class Place(floor: Floor, point: Point)

  case class Meeting(from: LocalDateTime, to: LocalDateTime) {
    def duration: Long = Math.abs(Duration.between(from, to).getSeconds)

    def combineIfOverlaps(other: Meeting): Option[Meeting] =
      if (from.compareTo(other.from) <= 0) {
        if (to.compareTo(other.to) >= 0) Some(this)
        else if (to.compareTo(other.from) < 0) None
        else Some(Meeting(from, other.to))
      } else other.combineIfOverlaps(this)

    def pass(threshold: Long): Boolean =
      duration >= threshold
  }

  object Meeting {
    def apply(args: (LocalDateTime, LocalDateTime)) = new Meeting(args._1, args._2)
  }

  def timestamp(s: String): LocalDateTime = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

  def apply(ts: LocalDateTime, x: Double, y: Double,
            floor: Int, uid: UID): Trace =
    Trace(uid, T(ts, Place(floor, Point(x,y))))

  def apply(array: Array[String]): Trace = {
    Trace(timestamp(array(0).init),
      array(1).toDouble, array(2).toDouble,
      array(3).toInt, array(4))
  }

  def apply(line: String): Trace =
    apply(line.split(','))

}