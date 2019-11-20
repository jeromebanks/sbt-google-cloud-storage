package com.lightbend.gcsplugin

import java.util
import java.io._
import java.io.FileInputStream

import scala.collection.JavaConverters._
import com.google.cloud.storage._
import com.google.cloud.storage.Storage
import com.google.cloud.storage.Bucket._
import com.google.cloud.storage.Storage.BlobListOption
import com.lightbend.gcsplugin.AccessRights._
import org.apache.ivy.core.module.descriptor._
import org.apache.ivy.plugins.repository._
import sbt.librarymanagement.RawRepository

case class GCSRepository(bucketName: String, path: String, publishPolicy: AccessRights) extends AbstractRepository {
  private val storage: Storage = StorageOptions.getDefaultInstance.getService
  private lazy val bucket = storage.get(bucketName)

  override def getResource(source: String): GCSResource = {
    GCSResource.create(storage, bucketName, path + "/" + source)
  }

  override def get(source: String, destination: File): Unit = {

    val extSource = if (destination.toString.endsWith("sha1"))
      source + ".sha1"
    else if (destination.toString.endsWith("md5"))
      source + ".md5"
    else
      source

    GCSResource.toFile(storage, GCSResource.create(storage, bucketName, extSource), destination)
  }

  override def list(parent: String): util.List[String] = {
    val filter = BlobListOption.prefix(path)
    val results = storage.list(bucketName, filter).getValues.asScala.map(_.getName).toList.asJava
    scala.Console.println(s" GCS RESULTS ARE ${results.asScala.mkString(":")}")
    val results2 = storage.list(bucketName).getValues.asScala.map(_.getName).toList.asJava
    scala.Console.println(s" GCS RESULTS UNFILTERED ARE ${results2.asScala.mkString(":")}")
    results
  }

  override def put(artifact: Artifact, source: File, destination: String, overwrite: Boolean): Unit = {

    publishPolicy match {
      case AccessRights.PublicRead ⇒
        bucket.create(
          (path + "/" + destination).replace("//", "/"),
          new FileInputStream(source),
          getContentType(artifact.getType),
          BlobWriteOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ)
        )
      case AccessRights.InheritBucket ⇒
        bucket.create(
          (path + "/" + destination).replace("//", "/"),
          new FileInputStream(source),
          getContentType(artifact.getType)
        )
    }
  }

  private def getContentType(ext: String): String = {

    ext.toLowerCase match {
      case "jar"  ⇒ "application/java-archive"
      case "xml"  ⇒ "application/xml"
      case "sha1" ⇒ "text/plain"
      case "md5"  ⇒ "text/plain"
      case "ivy"  ⇒ "application/xml"
      case _      ⇒ "application/octet-stream"
    }
  }
}
