package com.rajeev

import akka.actor.{Actor, ActorLogging}
import better.files.Dsl.SymbolicOperations
import better.files._
import com.rajeev.models.Models.{ErrorLogFile, ExtractedFile}

/**
  * Created by rajeevprasanna on 9/21/17.
  */
class ErrorLogExtractor extends Actor with ActorLogging with ConfigInitialzer {

  val interestedPatterns = getStringList("logs.wanted.patterns")
  val unwantedPatterns = getStringList("logs.unwanted.patterns")
  val separator = "\n\n===================================================================================================\n\n\n"

  var accumulatedErrorRecord = ""

  override def receive: Receive = {
    case errorLogFile: ExtractedFile =>
              log.info(s"Receive log file for error patterns extraction. logFileName => ${errorLogFile.file.name}")
              sender ! extractPatternRecords(errorLogFile)
  }

  def extractPatternRecords = (extractedFile: ExtractedFile) => {
    val file = extractedFile.file
    val lines:Iterator[String] = file.lineIterator

    //Create a new file to record all errors
    val fileName = s"analytics${file.name}"
    val errorLogFile:File = file"/tmp/$fileName"
    errorLogFile.delete(true)
    errorLogFile.createIfNotExists()

    for {l <- lines} processLogRow(l, errorLogFile)

    ErrorLogFile(errorLogFile, extractedFile)
  }

  def processLogRow = (row:String, errorLogFile:File) => {
    interestedPatterns.exists(row.contains(_)) match {
      case true  => if(!accumulatedErrorRecord.isEmpty)  writeToFile(accumulatedErrorRecord, errorLogFile)
        accumulatedErrorRecord = row

      case false if unwantedPatterns.exists(row.contains(_))  =>
              if(!accumulatedErrorRecord.isEmpty) writeToFile(accumulatedErrorRecord, errorLogFile)
              accumulatedErrorRecord = ""

      case false if !accumulatedErrorRecord.isEmpty => accumulatedErrorRecord = s"$accumulatedErrorRecord\n$row"
      case _ => //ignore this state.
    }
  }

  def writeToFile = (record:String, errorLogFile:File) => errorLogFile << record << separator
}
