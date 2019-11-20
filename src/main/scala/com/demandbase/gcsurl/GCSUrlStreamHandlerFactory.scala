package com.demandbase.gcsurl

import java.net.{ URLStreamHandler, URLStreamHandlerFactory }

object GCSUrlStreamHandlerFactory extends URLStreamHandlerFactory {

  override def createURLStreamHandler(protocol: String): URLStreamHandler = {
    protocol match {
      case "gs"  ⇒ GCSUrlStreamHandler
      case "gcs" ⇒ GCSUrlStreamHandler
      case "gfs" ⇒ GCSUrlStreamHandler
      ///case _ => super.createURLStreamHandler(protocol)
      case _     ⇒ null
    }
  }
}
