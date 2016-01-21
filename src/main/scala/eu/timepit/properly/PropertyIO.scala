package eu.timepit.properly

import eu.timepit.properly.PropertyOp._
import scala.util.Properties
import scalaz._
import scalaz.concurrent.Task
import scalaz.effect.IO

object PropertyIO {
  type PropertyIO[A] = Free[PropertyOp.Coyo, A]

  // constructors

  def clear(key: String): PropertyIO[Unit] =
    Free.liftFC(Clear(key))

  def get(key: String): PropertyIO[Option[String]] =
    Free.liftFC(Get(key))

  def getOrElse(key: String, defaultValue: String): PropertyIO[String] =
    get(key).map(_.getOrElse(defaultValue))

  def set(key: String, value: String): PropertyIO[Unit] =
    Free.liftFC(Set(key, value))

  // syntax

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

  implicit class PropertyIOSyntax[A](val self: PropertyIO[A]) extends AnyVal {
    def runIO: IO[A] =
      Free.runFC(self)(propertyOpToIO)

    def runTask: Task[A] =
      Task.delay(runIO.unsafePerformIO())

    def runMock(props: Map[String, String]): (Map[String, String], A) = {
      val state = Free.runFC(self)(propertyOpToMockState)
      state(props)
    }
  }

  // instances

  implicit val propertyIOMonad: Monad[PropertyIO] =
    Free.freeMonad[PropertyOp.Coyo]
}
