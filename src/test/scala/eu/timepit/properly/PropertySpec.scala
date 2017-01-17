package eu.timepit.properly

import eu.timepit.properly.Property._
import org.scalacheck.Prop._
import scala.util.{ Properties, Random }

class PropertySpec extends org.scalacheck.Properties("Property") {
  val empty = Map.empty[String, String]

  property("clear") = secure {
    Property.clear("foo").runMock(empty) == ((empty, ()))
  }

  property("get None") = secure {
    Property.get("foo").runMock(empty) == ((empty, None))
  }

  property("get Some") = secure {
    val m = Map("foo" -> "bar")
    Property.get("foo").runMock(m) == ((m, Some("bar")))
  }

  property("set") = secure {
    Property.set("foo", "bar").runMock(empty) == ((Map("foo" -> "bar"), ()))
  }

  property("set twice") = secure {
    val p = Property.set("foo", "bar")
      .flatMap(_ => Property.set("one", "two"))

    p.runMock(empty) == ((Map("foo" -> "bar", "one" -> "two"), ()))
  }

  property("set, get, map") = secure {
    val p = Property.set("one", "1")
      .flatMap(_ => Property.getOrElse("one", "0"))
      .map(_.toInt)

    p.runMock(empty) == ((Map("one" -> "1"), 1))
  }

  property("getAsIntOrElse") = secure {
    val m = Map("foo" -> "123", "bar" -> "1,5")
    val p = for {
      i1 <- Property.getAsIntOrElse("foo", 456)
      i2 <- Property.getAsIntOrElse("bar", 789)
    } yield (i1, i2)

    p.runMock(m) == ((m, (123, 789)))
  }

  property("getOrSet") = secure {
    val m = Map("foo" -> "bar")
    val p = for {
      v1 <- Property.getOrSet("foo", "baz")
      v2 <- Property.getOrSet("fuu", "baz")
    } yield (v1, v2)

    p.runMock(m) == ((Map("foo" -> "bar", "fuu" -> "baz"), ("bar", "baz")))
  }

  def testKeyValue: (String, String) =
    ("properly.test." + scala.math.abs(Random.nextInt), Random.nextString(8))

  property("clear IO") = secure {
    val (key, value) = testKeyValue
    Properties.setProp(key, value)
    Property.clear(key).runIO.unsafePerformIO()
    !Properties.propIsSet(key)
  }

  property("get IO") = secure {
    val (key, value) = testKeyValue
    Properties.setProp(key, value)
    Property.get(key).runIO.unsafePerformIO().getOrElse("") == value
  }

  property("set IO") = secure {
    val (key, value) = testKeyValue
    Property.set(key, value).runIO.unsafePerformIO()
    Properties.propOrEmpty(key) == value
  }

  property("set Task") = secure {
    val (key, value) = testKeyValue
    Property.set(key, value).runTask.run
    Properties.propOrEmpty(key) == value
  }
}
