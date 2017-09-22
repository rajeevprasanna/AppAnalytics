package com.rajeev

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.rajeev.models.Models._
import com.rajeev.utils.MailGunUtils
import better.files._

import ActorCommands._

/**
  * Created by rajeevprasanna on 9/19/17.
  */
class FileReader extends Actor with ActorLogging {

  val fileExtractor = context.actorOf(Props[FileExtractor], "file-extractor")
  val errorFileExtractor = context.actorOf(Props[ErrorLogExtractor], "error-file-extractor")

  var supervisorActor:Option[ActorRef] = None

  override def receive: Receive = {
    case file:CompressedLogFile =>
            log.info("received file for error extraction. fileName => " + file.name)
             supervisorActor = Some(sender)
            fileExtractor ! file

    case extractedLogFile:ExtractedFile =>
            log.info(s"received extracted log file with name => ${extractedLogFile.name}")
            errorFileExtractor ! extractedLogFile

    case errorLogFile:ErrorLogFile =>
            log.info(s"received error log file with name => ${errorLogFile.name}")
            MailGunUtils.sendEmail(errorLogFile.toFile)
            log.info(s"mail sent to user with attached error log file")

            //delete temp processed file after emailing user
            val f = file"${errorLogFile.fullPath}"
            f.delete(true)

           supervisorActor.map(_ ! ANALYTICS_PROCESS_DONE)

    case x => log.error(s"got event => $x")
  }

}
