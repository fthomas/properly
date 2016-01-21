package eu.timepit.properly

import eu.timepit.properly.PropertyIO._
import org.scalacheck.Prop._
import scala.util.{ Properties, Random }

class PropertyIOSpec extends org.scalacheck.Properties("PropertyIO") {
  val empty = Map.empty[String, String]

  property("clear") = secure {
    PropertyIO.clear("foo").runMock(empty) == ((empty, ()))
  }

  property("get None") = secure {
    PropertyIO.get("foo").runMock(empty) == ((empty, None))
  }

  property("get Some") = secure {
    val m = Map("foo" -> "bar")
    PropertyIO.get("foo").runMock(m) == ((m, Some("bar")))
  }

  property("set") = secure {
    PropertyIO.set("foo", "bar").runMock(empty) == ((Map("foo" -> "bar"), ()))
  }

  property("set twice") = secure {
    val p = PropertyIO.set("foo", "bar")
      .flatMap(_ => PropertyIO.set("one", "two"))

    p.runMock(empty) == ((Map("foo" -> "bar", "one" -> "two"), ()))
  }

  property("set, get, map") = secure {
    val p = PropertyIO.set("one", "1")
      .flatMap(_ => PropertyIO.getOrElse("one", "0"))
      .map(_.toInt)

    p.runMock(empty) == ((Map("one" -> "1"), 1))
  }

  property("clear IO") = secure {
    val key = "properly.test"
    val value = Random.nextString(8)

    Properties.setProp(key, value)
    PropertyIO.clear(key).runIO.unsafePerformIO()
    !Properties.propIsSet(key)
  }

  property("get IO") = secure {
    val key = "properly.test"
    val value = Random.nextString(8)

    Properties.setProp(key, value)
    PropertyIO.get(key).runIO.unsafePerformIO().contains(value)
  }

  property("set IO") = secure {
    val key = "properly.test"
    val value = Random.nextString(8)

    PropertyIO.set(key, value).runIO.unsafePerformIO()
    Properties.propOrEmpty(key) == value
  }

  property("set Task") = secure {
    val key = "properly.test"
    val value = Random.nextString(8)

    PropertyIO.set(key, value).runTask.run
    Properties.propOrEmpty(key) == value
  }
}
