package com.rajeev

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import better.files._
import com.rajeev.ActorCommands._
import com.rajeev.models.Models.CompressedLogFile
import com.rajeev.utils.{FileUtils, MailGunUtils}

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by rajeevprasanna on 9/21/17.
  */
class SupervisorActor extends Actor with ConfigInitialzer with ActorLogging {

  val scheduler = system.scheduler.schedule(0 milliseconds, 12 hours, self, TRIGGER_APP_ANALYTICS)
  val fileReaderActor = system.actorOf(Props[FileReader], "file-reader-actor")

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 60 minutes) {
      case ex: Exception => mailLogError(s"Exception occurred in child actor. ex => ${ex.getLocalizedMessage}")
                            Restart
    }

  val logFileDirectory:String = getString("app.logFileDirectory")
  val logFileExtensions:List[String] = getStringList("app.fileExtensions")
  val logFilePrefixes:List[String] = getStringList("app.logFilePrefixes")

  var unprocessedFiles = List.empty[File]
  var processingFiles = List.empty[File]

  override def receive: Receive = {

    case TRIGGER_APP_ANALYTICS if !unprocessedFiles.isEmpty =>
            mailLogError(s"files are not processed in the previous iteration. Immediately check the reason. current files => ${unprocessedFiles}")

    case TRIGGER_APP_ANALYTICS =>
      log.info(s"triggered app analytics")
      processingFiles = List.empty[File]
      FileUtils.getListOfFiles(logFileDirectory, logFileExtensions, logFilePrefixes) match {
        case Success(fs) => unprocessedFiles = unprocessedFiles ::: fs
                            self ! ANALYTICS_PROCESS_DONE

        case Failure(ex) => mailLogError(s"Error in reading files in configured directory. exception => ${ex.getLocalizedMessage}")
      }

    case ANALYTICS_PROCESS_DONE if !unprocessedFiles.isEmpty =>
            log.info(s"ANALYTICS_PROCESS_DONE event is received")
            unprocessedFiles match {
              case head :: tail =>
                          unprocessedFiles = tail
                          processingFiles = head :: processingFiles
                          fileReaderActor ! CompressedLogFile(head)
              case m => assert(false, s"this is unreachable case. message => $m")
            }

    case m if m != ANALYTICS_PROCESS_DONE => log.error(s"received some junk message. $m")
  }

  def mailLogError = (errorMsg:String) => {
    log.error(errorMsg)
    MailGunUtils.sendTextMail(errorMsg)
  }

}
