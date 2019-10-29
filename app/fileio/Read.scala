package fileio

import scala.util.{Success, Try}

trait Read[A] { def reads(s: String): Try[A] }

object Read {
  def apply[A](implicit readA: Read[A]): Read[A] = readA

  implicit object stringRead extends Read[String] {
    def reads(s: String): Try[String] = Success(s)
  }

  implicit object intRead extends Read[Int] {
    def reads(s: String) = Try(s.toInt)
  }

  implicit object longRead extends Read[Long] {
    def reads(s: String) = Try(s.toLong)
  }

  implicit object floatRead extends Read[Float] {
    def reads(s: String) = Try(s.toFloat)
  }

  implicit object doubleRead extends Read[Double] {
    def reads(s: String) = Try(s.toDouble)
  }
}
