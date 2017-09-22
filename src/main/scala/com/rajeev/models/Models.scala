package com.rajeev.models

import better.files._

/**
  * Created by rajeevprasanna on 9/19/17.
  */
object Models {

  final case class CompressedLogFile(file:File)
  object CompressedLogFile {
    def apply(file:File): CompressedLogFile = new CompressedLogFile(file)
  }

  final case class ExtractedFile(file:File, compressedLogFile:CompressedLogFile)
  object ExtractedFile {
    def apply(extractedFile:File, compressedFile:CompressedLogFile): ExtractedFile = new ExtractedFile(extractedFile, compressedFile)
  }

  final case class ErrorLogFile(file:File, extractedFile: ExtractedFile)
  object ErrorLogFile {
    def apply(errorLogFile:File, extractedFile:ExtractedFile): ErrorLogFile = new ErrorLogFile(errorLogFile, extractedFile)
  }

}
