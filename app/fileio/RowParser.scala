package fileio

import shapeless._

import scala.util.Try

/*
https://stackoverflow.com/questions/27781020/read-csv-in-scala-into-case-class-instances-with-error-handling
 */

trait RowParser[A] {
  def apply[L <: HList](row: List[String])(implicit
                                           gen: Generic.Aux[A, L],
                                           fromRow: FromRow[L]
  ): Try[A] = fromRow(row).map(gen.from)
}

object RowParser {
  def rowParserFor[A] = new RowParser[A] {}
}

