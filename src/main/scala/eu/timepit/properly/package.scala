package eu.timepit

import scalaz.Free

package object properly {
  type Property[A] = Free[PropertyOp.FreeFunctor, A]
}
