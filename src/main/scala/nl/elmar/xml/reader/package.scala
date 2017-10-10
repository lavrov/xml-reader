package nl.elmar.xml

import cats.FlatMap
import cats.data.{Kleisli, Validated, ValidatedNel}

import scala.xml._
import scala.util.control.Exception

package object reader {

  type Reader[A] = Kleisli[Result, NodeSeq, A]

  case class ReaderError(path: XmlPath, message: String)

  type Result[A] = ValidatedNel[ReaderError, A]


  def valid[A](a: A): Result[A] = Validated.valid(a)

  def invalid(error: String, path: XmlPath = XmlPath.__): Result[Nothing] =
    Validated invalidNel ReaderError(path, error)

  implicit val resultMonad = new FlatMap[Result] {

    override def map[A, B](fa: Result[A])(f: (A) => B): Result[B] = fa match {
      case Validated.Valid(a) => Validated.Valid(f(a))
      case invalid@Validated.Invalid(_) => invalid
    }

    override def flatMap[A, B](fa: Result[A])(f: (A) => Result[B]): Result[B] = fa match {
      case Validated.Valid(a) => f(a)
      case Validated.Invalid(e) => Validated.invalid(e)
    }

    override def tailRecM[A, B](a: A)(f: (A) => Result[Either[A, B]]): Result[B] = ???
  }

  implicit val stringReader: Reader[String] = Reader {
    case nodeSeq: NodeSeq if nodeSeq.size == 1 =>
      nodeSeq.head.child.headOption match {
        case Some(Text(value)) => valid(value)
        case None => valid("")
        case Some(_) => invalid("not a text node")
      }
    case _ =>
      invalid("there must be one node containing Text node inside")
  }

  implicit val longReader: Reader[Long] = stringReader.andThen(
    string =>
      Exception.nonFatalCatch.either(string.toLong)
        .fold(
          _ => invalid(s"'$string' cannot be interpeted as long"),
          valid
        )
  )

  implicit val intReader: Reader[Int] = longReader.map(_.toInt)

  implicit val booleanReader: Reader[Boolean] = stringReader.flatMapF {
    case "true" => valid(true)
    case "false" => valid(false)
    case string => invalid(s"cannot interpret $string as Boolean")
  }

  implicit def listReader[A: Reader]: Reader[List[A]] = XmlPath.__.list[A]

  implicit def optionReader[A: Reader]: Reader[Option[A]] = XmlPath.__.optional[A]

  def attribute(name: String): Reader[String] = Reader(
    _ \ s"@$name" match {
      case Group(Seq(Text(value))) => valid(value)
      case _ => invalid(s"attr $name is missing")
    }
  )
}

