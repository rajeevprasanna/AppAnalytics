package com.rajeev

import java.io.File

import akka.actor.{Actor, ActorLogging}
import com.rajeev.models.Models._
import com.rajeev.utils.FileUtils

/**
  * Created by rajeevprasanna on 9/19/17.
  */
class FileExtractor extends Actor with ActorLogging {

  override def receive: Receive = {
    case logfile:CompressedLogFile =>
          val file = new File(logfile.fullPath)
          val extractedFile = FileUtils.extractFile(file.getName, file.getParentFile.getPath)
          sender ! ExtractedFile(extractedFile.getName, extractedFile.getParentFile.getPath)
  }

}
