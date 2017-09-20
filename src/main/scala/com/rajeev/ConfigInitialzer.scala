package com.rajeev

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * Created by rajeevprasanna on 9/19/17.
  */
trait ConfigInitialzer {

  val config = ConfigFactory.load()
  implicit val system = ActorSystem("AppAnalytics", config)
  implicit val executionContext = system.dispatcher

  import scala.collection.JavaConversions._
  def getStringList(key:String):List[String] = config.getStringList(key).toList
  def getString(key:String):String = config.getString(key)
}
