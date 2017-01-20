package eu.timepit.properly

import scala.util.Properties
import scalaz.{ ~>, State }
import scalaz.effect.IO

sealed trait PropertyOp[A]

object PropertyOp {
  case class Clear(key: String) extends PropertyOp[Unit]
  case class Get(key: String) extends PropertyOp[Option[String]]
  case class Set(key: String, value: String) extends PropertyOp[Unit]

  // interpreters

  val propertyOpToIO: PropertyOp ~> IO =
    new (PropertyOp ~> IO) {
      def apply[A](op: PropertyOp[A]): IO[A] =
        op match {
          case Clear(key) => IO { Properties.clearProp(key); () }
          case Get(key) => IO { Properties.propOrNone(key) }
          case Set(key, value) => IO { Properties.setProp(key, value); () }
        }
    }

  type MockState[A] = State[Map[String, String], A]
  val propertyOpToMockState: PropertyOp ~> MockState =
    new (PropertyOp ~> MockState) {
      def apply[A](op: PropertyOp[A]): MockState[A] =
        op match {
          case Clear(key) => State.modify(_ - key)
          case Get(key) => State.gets(_.get(key))
          case Set(key, value) => State.modify(_.updated(key, value))
        }
    }
}
