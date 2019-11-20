package com.demandbase.gcsurl

import java.net.{ URL, URLConnection, URLStreamHandler }

import com.google.cloud.storage.StorageOptions

object GCSUrlStreamHandler extends URLStreamHandler {

  override def openConnection(u: URL): URLConnection = {
    Console.println(s" Opening GCS Connection ${u}")

    return new GCSUrlConnection(u, storage)
  }

  val storage = StorageOptions.getDefaultInstance.getService
}
