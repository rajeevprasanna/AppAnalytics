package com.rajeev.temp.basket

import akka.serialization.Serializer
import BasketModels._

import spray.json._

/**
  * Created by rajeevprasanna on 9/18/17.
  */
class BasketEventSerializer extends Serializer{
     import JsonFormats._

  // Completely unique value to identify this implementation of Serializer, used to optimize network traffic.
  // Values from 0 to 16 are reserved for Akka internal usage.
  // Make sure this does not conflict with any other kind of serializer or you will have problems
  override def identifier: Int = 90020001

  // This implementation of the serializer does not need a manifest
  override def includeManifest: Boolean = false

  // JSON -> scala
  // i have tightened the return type from AnyRef to Event
  override def fromBinary(bytes: Array[Byte], manifest: Option[Class[_]]): AnyRef = {
    val jsonAst = new String(bytes).parseJson
    BasketEventFormat.read(jsonAst)
  }

  override def toBinary(scalaObject: AnyRef): Array[Byte] = {
    scalaObject match {
      case e: Event => BasketEventFormat.write(e).compactPrint.getBytes
      case other => serializationError(s"Cannot serialize Basket Event: $other with $getClass")
    }
  }
}
