import Trace._

sealed trait Distance {
  def pass(threshold: Double): Boolean
}

object Distance {

  case object NotDetermined extends Distance {
    override def pass(threshold: Double): Boolean = false
  }

  case class Determined(r: Double) extends Distance {
    override def pass(threshold: Double): Boolean = r < threshold
  }

  def distance(a: Point, b: Point): Double = {
    def square(x: Double): Double = x * x

    Math.sqrt {
      square(a.x - b.x) + square(a.y - b.y)
    }
  }

  def determine(a: Place, b: Place): Distance =
    if (a.floor == b.floor) {
      Determined(distance(a.point, b.point))
    } else {
      NotDetermined
    }

  def determine(a: Option[Place], b: Option[Place]): Distance =
    a match {
      case Some(placeA) => b match {
        case Some(placeB) => determine(placeA, placeB)
        case None => NotDetermined
      }
      case None => NotDetermined
    }

}