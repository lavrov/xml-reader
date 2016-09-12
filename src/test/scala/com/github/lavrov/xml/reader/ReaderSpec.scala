package com.github.lavrov.xml.reader

import org.scalatest.WordSpec
import org.scalatest.Matchers._

class ReaderSpec extends WordSpec {

  def provide = afterWord("provide")
  def support = afterWord("support")
  def read = afterWord("read")

  "Reader" should provide {
      "readers for primitive types" that read {
        "string" in {
          stringReader(<node>content</node>) should be (
            valid("content")
          )
        }
        "long" in {
          longReader(<node>4234</node>) should be (
            valid(4234)
          )
        }
        "boolean" in {
          booleanReader(<node>true</node>) should be (
            valid(true)
          )
          booleanReader(<node>false</node>) should be (
            valid(false)
          )
        }
      }
      "readers for List and Option" that read {
        "List[A: Reader]" in {
          val reader = listReader[Long]
          reader(
              <item>1</item>
              <item>2</item>
              <item>3</item>
          ) should be (
            valid(List(1, 2, 3))
          )
        }
        "Option[A: Reader]" in {
          val reader = optionReader[Boolean]
          reader(Nil) shouldBe valid(None)
          reader(<node>true</node>) shouldBe valid(Some(true))
        }
      }
    }

   "Reader" should support {
      "cartesian syntax because it is Applicative" in {
        import XmlPath.__
        import cats.syntax.cartesian._
        val reader: Reader[(String, Int)] =
        (__ \ "name")[String] |@|
        (__ \ "age")[Int] map ((_, _))
        reader(
          <person>
            <name>John</name>
            <age>39</age>
          </person>
        ) shouldBe valid(("John", 39))
      }
      "composition because it is Kleisli" in {
        val reader: Reader[Boolean] = longReader.map( n => if (n > 10) true else false)
        reader(<node>11</node>) should be (valid(true))
        reader(<node>5</node>) should be (valid(false))
      }
    }
}
