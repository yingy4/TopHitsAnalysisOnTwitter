package Ingest

import scala.io.Source
import scala.util.Try

class Ingest[T: Ingestible] extends (){

}

trait Ingestible[X] {
  def fromString(w: String): Try[X]
}
