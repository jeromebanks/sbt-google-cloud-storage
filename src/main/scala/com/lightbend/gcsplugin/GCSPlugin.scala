package com.lightbend.gcsplugin

import java.net.URL

import com.demandbase.gcsurl.GCSUrlStreamHandlerFactory
import sbt.PluginTrigger
import sbt.AutoPlugin
import sbt.util.Logger

object GCSPlugin extends AutoPlugin {

  val bootstrapStartup = {
    scala.Console.println(s" Bootstrapping GCS URL Plugin")
    URL.setURLStreamHandlerFactory(GCSUrlStreamHandlerFactory)
  }

  trait Keys {
    implicit def toSbtResolver(resolver: GCSResolver): sbt.Resolver = {
      new sbt.RawRepository(resolver, resolver.getName)
    }
  }

  object Keys extends Keys
  object autoImport extends Keys {
    val GCSResolver = com.lightbend.gcsplugin.GCSResolver
    val GCSPublisher = com.lightbend.gcsplugin.GCSPublisher
    val AccessRights = com.lightbend.gcsplugin.AccessRights
  }

  import sbt.Keys._

  override def projectSettings = Seq( ////publishMavenStyle := false
  ////publishMavenStyle := true
  )

  override def trigger: PluginTrigger = allRequirements
}
