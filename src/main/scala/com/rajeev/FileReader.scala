package com.rajeev

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import better.files._
import com.rajeev.ActorCommands._
import com.rajeev.models.Models._
import com.rajeev.utils.MailGunUtils

/**
  * Created by rajeevprasanna on 9/19/17.
  */
class FileReader extends Actor with ActorLogging with ConfigInitialzer {

  val fileExtractor = context.actorOf(Props[FileExtractor], "file-extractor")
  val errorFileExtractor = context.actorOf(Props[ErrorLogExtractor], "error-file-extractor")
  val processedFilesDirectory = file"${getString("app.processedLogDirectory")}"

  var supervisorActor:Option[ActorRef] = None

  override def preStart(): Unit = {
    super.preStart()
    if(!processedFilesDirectory.exists) processedFilesDirectory.createDirectory()
  }

  override def receive: Receive = {
    case compressedLogFile:CompressedLogFile =>
            log.info("received file for error extraction. fileName => " + compressedLogFile.file.name)
             supervisorActor = Some(sender)
            fileExtractor ! compressedLogFile

    case extractedLogFile:ExtractedFile =>
            log.info(s"received extracted log file with name => ${extractedLogFile.file.name}")
            errorFileExtractor ! extractedLogFile

    case errorLogFile:ErrorLogFile =>
            log.info(s"received error log file with name => ${errorLogFile.file.name}")
            MailGunUtils.sendEmail(errorLogFile.file)
            log.info(s"mail sent to user with attached error log file")

            //Move the log file to processed logs directory
            val compressedLogFile = errorLogFile.extractedFile.compressedLogFile.file
            compressedLogFile.moveTo(file"$processedFilesDirectory/${compressedLogFile.name}", true)

            //delete temp processed file after emailing user
            errorLogFile.extractedFile.file.delete(true)
            errorLogFile.file.delete(true)

           supervisorActor.map(_ ! ANALYTICS_PROCESS_DONE)

    case x => log.error(s"got event => $x")
  }

}
