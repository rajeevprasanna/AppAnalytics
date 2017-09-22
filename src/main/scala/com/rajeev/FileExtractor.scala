package com.rajeev

import akka.actor.{Actor, ActorLogging}
import better.files._
import com.rajeev.models.Models._
import com.rajeev.utils.FileUtils

/**
  * Created by rajeevprasanna on 9/19/17.
  */
class FileExtractor extends Actor with ActorLogging with ConfigInitialzer {

  val outputDirectory = getString("app.outputDirectory")

  override def preStart(): Unit = {
      super.preStart()
      val directory = file"$outputDirectory"
      if(!directory.exists) directory.createDirectory()
  }

  override def receive: Receive = {
    case logfile:CompressedLogFile => sender ! FileUtils.extractFile(logfile, outputDirectory)
  }
}
