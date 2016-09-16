package com.github.lavrov.xml.reader

import cats.Applicative
import com.github.lavrov.xml.reader.XmlPath.__
import shapeless.labelled.FieldType
import shapeless.{::, HList, HNil, LabelledGeneric, Witness}

object GenericReader {

  implicit def fieldTypeReader[K <: Symbol, V](
      implicit
      witness: Witness.Aux[K],
      vReader: Reader[V]
  ): Reader[FieldType[K, V]] = {
    (__ \ witness.value.name).read[V].asInstanceOf[Reader[FieldType[K, V]]]
  }

  implicit def hNilReader: Reader[HNil] = Reader.pure(HNil)

  implicit def hListReader[H, T <: HList](
      implicit
      hReader: Reader[H],
      tReader: Reader[T]
  ): Reader[H :: T] = {
    Applicative[Reader].product(hReader, tReader).map {
      case (head, tail) => head :: tail
    }
  }

  implicit def genericReader[A, Repr](
      implicit
      gen: LabelledGeneric.Aux[A, Repr],
      reader: Reader[Repr]
  ): Reader[A] = reader.map(gen.from)

}
