package nl.elmar.xml.reader

import org.scalatest.WordSpec
import org.scalatest.Matchers._

class XmlPathSpec extends WordSpec {
  import XmlPath.__

  "XmlPath" should {
    "select and read subnodes" in {
      val reader = (__ \ "item")[List[Boolean]]
      reader(
        <list>
          <item>true</item>
          <item>false</item>
          <item>false</item>
        </list>
        <list>
          <item>false</item>
          <item>true</item>
        </list>
      ) should be (
        valid(List(true, false, false, false, true))
      )
    }
  }

}
