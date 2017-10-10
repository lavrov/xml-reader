package nl.elmar.xml.reader

import org.scalatest.WordSpec
import org.scalatest.Matchers._

class GenericReaderSpec extends WordSpec {

  "GenericReader" should {
    "generate reader for case class" in {

      case class Book(title: String, year: Int, chapter: List[Chapter])
      case class Chapter(title: String)

      import GenericReader._

      val reader = Reader.reader[Book]

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

    "generate reader for library case class" in {
      case class Author(name: String)
      case class Book(title: String, year: Int, author: List[Author])
      case class Library(book: List[Book])

      import XmlPath.__
      import GenericReader._

      implicit val authorReader: Reader[Author] =
        __.read[String] map Author
      implicit val bookReader = Reader.reader[Book]
      val libraryReader = Reader.reader[Library]

      val result =
        libraryReader(
          <library>
            <book>
              <title>Programming in Scala, Third Edition</title>
              <year>2016</year>
              <author>Martin Odersky</author>
              <author>Lex Spoon</author>
              <author>Bill Venners</author>
            </book>
          </library>
        )
      result shouldBe valid(
        Library(
          Book(
            "Programming in Scala, Third Edition",
            2016,
              Author("Martin Odersky") ::
              Author("Lex Spoon") ::
              Author("Bill Venners") :: Nil
          ) :: Nil
        )
      )
    }
  }
}

