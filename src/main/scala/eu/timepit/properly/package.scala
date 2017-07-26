package eu.timepit

import cats.free.Free

package object properly {
  type Property[A] = Free[PropertyOp, A]
}
