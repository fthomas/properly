package eu.timepit.properly

import cats.effect.IO
import cats.free.Free
import scala.util.Try

object Property {

  // constructors

  def clear(key: String): Property[Unit] =
    Free.liftF(PropertyOp.Clear(key))

  def get(key: String): Property[Option[String]] =
    Free.liftF(PropertyOp.Get(key))

  def set(key: String, value: String): Property[Unit] =
    Free.liftF(PropertyOp.Set(key, value))

  // derived operations

  def getAsIntOrElse(key: String, defaultValue: Int): Property[Int] =
    get(key).map(_.flatMap(s => Try(s.toInt).toOption).getOrElse(defaultValue))

  def getOrElse(key: String, defaultValue: String): Property[String] =
    get(key).map(_.getOrElse(defaultValue))

  def getOrSet(key: String, value: String): Property[String] =
    get(key).flatMap {
      case Some(v) => Free.pure(v)
      case None => set(key, value).map(_ => value)
    }

  // syntax

  implicit class PropertySyntax[A](val self: Property[A]) extends AnyVal {
    def runIO: IO[A] =
      self.foldMap(PropertyOp.propertyOpToIO)

    def runMock(props: Map[String, String]): (Map[String, String], A) = {
      val state = self.foldMap(PropertyOp.propertyOpToMockState)
      state.run(props).value
    }
  }
}
