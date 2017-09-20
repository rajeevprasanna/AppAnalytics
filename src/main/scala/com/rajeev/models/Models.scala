package com.rajeev.models

import java.io.File

/**
  * Created by rajeevprasanna on 9/19/17.
  */
object Models {

  trait AnalyticsFile {
    def name:String
    def directory:String
    def fullPath = s"$directory/$name"
  }

  final case class CompressedLogFile(name:String, directory:String) extends AnalyticsFile
  object CompressedLogFile {
    def apply(file:File): CompressedLogFile = new CompressedLogFile(file.getName, file.getParentFile.getAbsolutePath)
    def apply(fileOp:Option[File]): Option[CompressedLogFile] = fileOp.map(apply(_))
  }

  final case class ExtractedFile(name:String, directory:String) extends AnalyticsFile
  object ExtractedFile {
    def apply(file:File): ExtractedFile = new ExtractedFile(file.getName, file.getParentFile.getAbsolutePath)
    def apply(fileOp:Option[File]): Option[ExtractedFile] = fileOp.map(apply(_))
  }

  final case class ErrorLogFile(name:String, directory:String) extends AnalyticsFile
  object ErrorLogFile {
    def apply(file:File): ErrorLogFile = new ErrorLogFile(file.getName, file.getParentFile.getAbsolutePath)
    def apply(fileOp:Option[File]): Option[ErrorLogFile] = fileOp.map(apply(_))
  }
}
