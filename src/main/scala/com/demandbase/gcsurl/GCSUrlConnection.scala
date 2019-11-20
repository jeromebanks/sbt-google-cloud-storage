package com.demandbase.gcsurl

import java.io.{ ByteArrayInputStream, IOException, InputStream, OutputStream }
import java.net.{ URL, URLConnection }

import com.google.cloud.storage.{ Blob, BlobId, BlobInfo, Storage }

class GCSUrlConnection(url: URL, val storage: Storage) extends URLConnection(url) {
  private var _blob: Option[Blob] = None

  val bucket = getBucket(url)
  val key = getKey(url)

  override def connect(): Unit = try {

    val blobId = BlobId.of(bucket, key)
    val blob = storage.get(blobId)
    if (blob != null) {
      _blob = Some(blob)
      connected = true
    }
  } catch {
    case unexpected: Throwable ⇒ {
      unexpected.printStackTrace()
      throw unexpected
    }
  }

  def getBucket(url: URL) = {
    url.getHost
  }

  def getKey(url: URL) = {
    url.getPath.substring(1)
  }

  override def getContentType: String = {
    if (!connected) connect
    _blob match {
      case Some(blob) ⇒ blob.getContentType
      case None       ⇒ null
    }
  }

  override def getContent = {
    if (!connected) connect
    _blob match {
      case Some(blob) ⇒ blob.getContent()
      case None       ⇒ null
    }
  }

  override def getContentEncoding: String = {
    if (!connected) connect
    _blob match {
      case Some(blob) ⇒ blob.getContentEncoding
      case None       ⇒ null
    }
  }

  override def getContentLength() = {
    if (!connected) connect
    _blob match {
      case Some(blob) ⇒ blob.getSize.toInt
      case None       ⇒ 0
    }
  }

  override def getInputStream: InputStream = try {
    if (!connected) connect

    _blob match {
      case Some(blob) ⇒ {
        val content = blob.getContent()
        if (content != null) {
          new ByteArrayInputStream(content)
        } else {
          emptyStream
        }
      }

      case None ⇒ emptyStream
    }

  } catch {
    case unexpected: Throwable ⇒ {
      unexpected.printStackTrace()
      throw unexpected
    }
  }

  private val emptyBytes = new Array[Byte](0)

  val emptyStream = new ByteArrayInputStream(emptyBytes)

}
