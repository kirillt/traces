import java.awt.image.BufferedImage
import java.awt.Color
import java.awt.geom._
import java.io.File
import javax.imageio.ImageIO

import Trace.{Floor, Place, Point, UID}

object DrawTraces extends TraceApp {

  if (FullGraph) {
    throw new IllegalArgumentException("Option FULL_GRAPH makes no sense for drawer")
  }

  val TargetDir = new File(sys.env.getOrElse("TARGET", s"draw-$PersonA-$PersonB"))

  type Trajectory   = Seq[Point]
  type Trajectories = Map[UID, Trajectory]

  case class Bounds(lx: Double, ly: Double, rx: Double, ry: Double) {
    def update(point: Point): Bounds = Bounds(
      lx = Math.min(lx, point.x), ly = Math.min(ly, point.y),
      rx = Math.max(rx, point.x), ry = Math.max(ry, point.y))
  }

  object Bounds {
    def apply(point: Point): Bounds = Bounds(point.x, point.y, point.x, point.y)
  }

  case class Plot(bounds: Bounds, trajectories: Trajectories)

  val plots: Map[Floor,Plot] = {
    traces(PersonA, PersonB).foldLeft(Map[Floor,Plot]()) {
      case (acc, Trace(uid, T(_, Place(floor, point)))) =>
        acc.get(floor) match {
          case Some(Plot(bounds,trajectories)) =>
            def wrap(tr: Trajectories) = Plot(bounds.update(point), tr)

            acc.updated(floor, trajectories.get(uid) match {
              case Some(trajectory) => wrap(trajectories.updated(uid, point +: trajectory))
              case None => wrap(trajectories.updated(uid, Seq(point)))
            })
          case None => acc.updated(floor, Plot(Bounds(point), Map(uid -> Seq(point))))
        }
    }
  }

  for ((floor, Plot(Bounds(lx, ly, rx, ry), trajectories)) <- plots) {
    val (width, height) = (960.0, 640.0)
    val (lrx, lry) = (rx - lx, ry - ly)

    def transformX(x: Double): Double = width * (x - lx) / lrx
    def transformY(y: Double): Double = height * (y - ly) / lry

    if (lrx != 0 && lry != 0) {
      val canvas = new BufferedImage(width.toInt, height.toInt, BufferedImage.TYPE_INT_RGB)
      val graph = canvas.createGraphics()

      graph.setColor(Color.BLACK)
      graph.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
      graph.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
        java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

      for ((uid, trajectory) <- trajectories) {
        val lines = trajectory.sliding(2).map {
          points => new Line2D.Double(
            transformX(points.head.x), transformY(points.head.y),
            transformX(points.last.x), transformY(points.last.y))
        }

        graph.setColor(if (uid == PersonA) Color.GREEN else Color.BLUE)
        lines.foreach { line =>
          graph.draw(line)
        }
      }

      graph.dispose()
      TargetDir.mkdirs()
      ImageIO.write(canvas, "png", new File(TargetDir, s"$floor-floor.png"))
    }

  }

}
