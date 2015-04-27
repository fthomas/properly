package eu.timepit.properly

import scalaz.Coyoneda

sealed trait PropertyOp[A]

object PropertyOp {
  case class Clear(key: String) extends PropertyOp[Unit]
  case class Get(key: String) extends PropertyOp[Option[String]]
  case class Set(key: String, value: String) extends PropertyOp[Unit]

  type Coyo[A] = Coyoneda[PropertyOp, A]
}
