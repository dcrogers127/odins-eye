package fileio

import shapeless._

import scala.util.{Failure, Success, Try}

trait FromRow[L <: HList] { def apply(row: List[String]): Try[L] }

object FromRow {
  import HList.ListCompat._

  def apply[L <: HList](implicit fromRow: FromRow[L]): FromRow[L] = fromRow

  def fromFunc[L <: HList](f: List[String] => Try[L]) = new FromRow[L] {
    def apply(row: List[String]) = f(row)
  }

  implicit val hnilFromRow: FromRow[HNil] = fromFunc {
    case Nil => Success(HNil)
    case _ => Failure(new RuntimeException("No more rows expected"))
  }

  implicit def hconsFromRow[H: Read, T <: HList: FromRow]: FromRow[H :: T] =
    fromFunc {
      case h :: t => for {
        hv <- Read[H].reads(h)
        tv <- FromRow[T].apply(t)
      } yield hv :: tv
      case Nil => Failure(new RuntimeException("Expected more cells"))
    }
}

