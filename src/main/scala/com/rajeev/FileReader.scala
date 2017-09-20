package com.rajeev

import java.io.File

import akka.actor.{Actor, ActorLogging, Props}
import com.rajeev.models.Models._
import com.rajeev.utils.{FileUtils, MailGunUtils}

/**
  * Created by rajeevprasanna on 9/19/17.
  */
class FileReader extends Actor with ActorLogging {

  override def receive: Receive = {
    case Some(file:CompressedLogFile) =>
            log.info("received file for error extraction. fileName => " + file.name)
            val fileExtractor = context.actorOf(Props[FileExtractor], "file-extractor")
            fileExtractor ! file

    case extractedLogFile:ExtractedFile =>
            log.info(s"received extracted log file with name => ${extractedLogFile.name}")
            MailGunUtils.sendEmail(extractedLogFile.toFile)

    case x => log.error(s"got event => $x")
  }

}
