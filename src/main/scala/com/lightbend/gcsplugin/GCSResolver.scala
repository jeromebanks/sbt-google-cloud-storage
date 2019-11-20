package com.lightbend.gcsplugin

import java.util.Date

import org.apache.ivy.core.module.descriptor.Artifact
import org.apache.ivy.core.module.id.ModuleRevisionId
import org.apache.ivy.plugins.repository.Repository
import org.apache.ivy.plugins.resolver._
import org.apache.ivy.plugins.resolver.util.{ ResolvedResource, ResourceMDParser }
import sbt.librarymanagement._

object AccessRights extends Enumeration {
  type AccessRights = Value
  val PublicRead, InheritBucket = Value
}
import com.lightbend.gcsplugin.AccessRights._

object GCSResolver {
  def forBucket(bucketName: String, path: String) = {
    new GCSResolver("Google Cloud Storage Resolver", bucketName, path, publishPolicy = AccessRights.InheritBucket)
  }
}

object GCSPublisher {
  def forBucket(bucketName: String, path: String, publishPolicy: AccessRights) = {
    new GCSResolver("Google Cloud Storage Publisher", bucketName, path: String, publishPolicy)
  }
}

class GCSResolver(name: String, bucketName: String, path: String, publishPolicy: AccessRights) extends URLResolver {
  setName(name)

  val gcsRepo = GCSRepository(bucketName, path, publishPolicy)

  override def setRepository(repo: Repository): Unit = {
    super.setRepository(gcsRepo)
  }

  override def getTypeName() = { "gcs" }

  override def getRepository: Repository = {
    gcsRepo
  }
  ///setRepository( RawRepository(GCSRepository(bucketName, path, publishPolicy), name))

  ////setM2compatible(false)
  setM2compatible(true)
  Resolver.ivyStylePatterns.ivyPatterns.foreach { p ⇒ this.addIvyPattern(p) }
  Resolver.ivyStylePatterns.artifactPatterns.foreach { p ⇒ this.addArtifactPattern(p) }

/***
  @Override
  override def listResources(rep: Repository, mrid: ModuleRevisionId, pattern: String, artifact: Artifact): Array[ResolvedResource] = {
    scala.Console.println(s" LIST RESOURCE REPOSITORY = ${rep} ${gcsRepo}")
    super.listResources(gcsRepo, mrid, pattern, artifact)
  }

  @Override
  override def findResourceUsingPattern(mrid: ModuleRevisionId, pattern: String, artifact: Artifact,
      rmdparser: ResourceMDParser,
      date: Date) = {
    Console.out.println(s" GCS FIND RESOURCE ${mrid} ${pattern}  ${artifact} ${rmdparser}")
    ///super.findResourceUsingPattern(mrid, pattern, artifact, rmdparser, date)
    val res = super.findResourceUsingPattern(mrid, pattern, artifact, rmdparser, date)
    Thread.dumpStack()
    Console.out.println(s" GCS RESOURCE is ${res}")
    res
  }
  **/

}
