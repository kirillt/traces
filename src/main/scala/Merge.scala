import scala.annotation.tailrec

object Merge {

  def betweenSeparators[X,Y](xs: Iterator[Option[X]])(f: (X, X) => Y): Iterator[Y] = new Iterator[Y] {
    val it: BufferedIterator[Option[X]] = xs.buffered

    override def next: Y = {
      it.next match {
        case Some(x) => merge(x, x)
        case None => throw new NoSuchElementException
      }
    }

    @tailrec
    override def hasNext: Boolean = it.headOption match {
      case Some(None) => it.next ; hasNext
      case Some(Some(_)) => true
      case None => false
    }

    @tailrec
    private def merge(x1: X, x2: X): Y = if (it.hasNext) {
      it.next match {
        case Some(x) => merge(x1, x)
        case None => f(x1, x2)
      }
    } else f(x1, x2)
  }

  def overlaps[X,Y](xs: Iterator[X])(f: (X, X) => Option[X]): Iterator[X] = new Iterator[X] {
    val it: BufferedIterator[X]= xs.buffered

    override def next: X =
      next(it.next)

    override def hasNext: Boolean = it.hasNext

    @tailrec
    def next(acc: X): X = {
      it.headOption match {
        case Some(x) => f(acc,x) match {
          case Some(xy) => it.next; next(xy)
          case None => acc
        }
        case None => acc
      }
    }
  }

}
