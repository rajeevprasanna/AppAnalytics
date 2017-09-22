package com.rajeev

import java.io.File

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorLogging, OneForOneStrategy, Props, SupervisorStrategy}
import com.rajeev.models.Models.CompressedLogFile
import com.rajeev.utils.FileUtils

import scala.concurrent.duration._
import scala.util.{Failure, Success}

import ActorCommands._

/**
  * Created by rajeevprasanna on 9/21/17.
  */
class SupervisorActor extends Actor with ConfigInitialzer with ActorLogging {

  val scheduler = system.scheduler.schedule(0 milliseconds, 12 hours, self, TRIGGER_APP_ANALYTICS)
  val fileReaderActor = system.actorOf(Props[FileReader], "file-reader-actor")

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 3, withinTimeRange = 60 minutes) {
      case ex: Exception => //TODO : send email to developers informing about this halt case.
            log.error(s"Exception occurred in child actor. ex => ${ex.getStackTrace}")
            Restart
    }

  val logFileDirectory:String = getString("app.logFileDirectory")
  val logFileExtensions:List[String] = getStringList("app.fileExtensions")
  val logFilePrefixes:List[String] = getStringList("app.logFilePrefixes")

  var unprocessedFiles = List[File]()
  var processingFiles = List[File]()

  override def receive: Receive = {

    case TRIGGER_APP_ANALYTICS if !unprocessedFiles.isEmpty =>
            log.error(s"files are not processed in the previous iteration. Immediately check the reason. current files => ${unprocessedFiles}")
            //TODO : send email to developers informing about this halt case.

    case TRIGGER_APP_ANALYTICS =>
      log.info(s"triggered app analytics")
      FileUtils.getListOfFiles(logFileDirectory, logFileExtensions, logFilePrefixes) match {
        case Success(fs) => unprocessedFiles = unprocessedFiles ::: fs
                            self ! ANALYTICS_PROCESS_DONE

        case Failure(ex) => log.error(s"Error in reading files in configured directory. exception => ${ex.getStackTrace}")
                            //TODO : send email to developers informing about this halt case.
      }

    case ANALYTICS_PROCESS_DONE if !unprocessedFiles.isEmpty =>
            log.info(s"ANALYTICS_PROCESS_DONE event is received")
            unprocessedFiles match {
              case head :: tail =>
                          unprocessedFiles = tail
                          processingFiles = head :: processingFiles
                          fileReaderActor ! CompressedLogFile(head)
              case _ => assert(false, "this is unreachable case")
            }

    case x => log.error(s"received some junk message. ${x}")
  }

}
