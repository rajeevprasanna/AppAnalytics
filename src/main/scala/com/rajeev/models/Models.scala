package com.rajeev.models

import better.files._

/**
  * Created by rajeevprasanna on 9/19/17.
  */
object Models {

  trait AnalyticsFile {
    def name:String
    def directory:String
    def fullPath = s"$directory/$name"
    def toFile = file"$fullPath"
  }

  final case class CompressedLogFile(name:String, directory:String) extends AnalyticsFile
  object CompressedLogFile {
    def apply(file:File): CompressedLogFile = new CompressedLogFile(file.name, file.parent.pathAsString)
  }

  final case class ExtractedFile(name:String, directory:String) extends AnalyticsFile
  object ExtractedFile {
    def apply(file:File): ExtractedFile = new ExtractedFile(file.name, file.parent.pathAsString)
  }

  final case class ErrorLogFile(name:String, directory:String) extends AnalyticsFile
  object ErrorLogFile {
    def apply(file:File): ErrorLogFile = new ErrorLogFile(file.name, file.parent.pathAsString)
  }
}
