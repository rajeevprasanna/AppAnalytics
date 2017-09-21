package com.rajeev

import akka.actor.{Actor, ActorLogging}
import com.rajeev.models.Models.ExtractedFile

import better.files._
import java.io.{File => JFile}

/**
  * Created by rajeevprasanna on 9/21/17.
  */
class ErrorLogExtractor extends Actor with ActorLogging with ConfigInitialzer {

  val interestedPatterns = getStringList("logs.wanted.patterns")
  val unwantedPatterns = getStringList("logs.unwanted.patterns")

  var accumulatedErrorRecord = ""

  override def receive: Receive = {
    case errorLogFile: ExtractedFile =>
              log.info(s"Receive log file for error patterns exraction. logFileName => ${errorLogFile.name}")
              val file:File = File("/Users/rajeevprasanna/Desktop/logFiles/test1.log")// File(errorLogFile.fullPath)
              extractPatternRecords(file)
  }

  def extractPatternRecords = (file:File) => {
    val lines:Iterator[String] = file.lineIterator
    for {l <- lines} processLogRow(l)
  }

  def processLogRow = (row:String) => {
    interestedPatterns.exists(row.contains(_)) match {
      case true  => if(!accumulatedErrorRecord.isEmpty)  println(accumulatedErrorRecord)
        accumulatedErrorRecord = row

      case false if unwantedPatterns.exists(row.contains(_))  =>
              if(!accumulatedErrorRecord.isEmpty) println(accumulatedErrorRecord)
              accumulatedErrorRecord = ""

      case false if !accumulatedErrorRecord.isEmpty => accumulatedErrorRecord = s"$accumulatedErrorRecord\n$row"
      case _ => //ignore this state.
        log.info("received unwanted row")
    }
  }
}
