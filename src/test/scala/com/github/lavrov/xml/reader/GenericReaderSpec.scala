package com.github.lavrov.xml.reader

import org.scalatest.WordSpec
import org.scalatest.Matchers._

class GenericReaderSpec extends WordSpec {

  "GenericReader" should {
    "generate reader for case class" in {

      case class Book(title: String, year: Int, chapter: List[Chapter])
      case class Chapter(title: String)

      import GenericReader._

      val reader = implicitly[Reader[Book]]

      val result =
        reader(
          <book>
            <title>Programming in Scala</title>
            <year>2016</year>
            <chapter>
              <title>One</title>
            </chapter>
            <chapter>
              <title>Two</title>
            </chapter>
          </book>
        )

      result shouldBe valid(
        Book("Programming in Scala", 2016, Chapter("One") :: Chapter("Two") :: Nil)
      )
    }
  }
}

