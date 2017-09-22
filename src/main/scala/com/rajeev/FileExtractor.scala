package com.rajeev

import better.files._

import akka.actor.{Actor, ActorLogging}
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
    case logfile:CompressedLogFile =>
          val file = file"${logfile.fullPath}"
          val extractedFile = FileUtils.extractFile(file.name, file.parent.pathAsString, outputDirectory)
          sender ! ExtractedFile(extractedFile.name, extractedFile.parent.pathAsString)
  }
}
