package com.rajeev.utils

import java.io.BufferedInputStream
import java.nio.file.Files

import better.files._
import com.rajeev.models.Models.{CompressedLogFile, ExtractedFile}
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream

import scala.util.Try

//Refer : https://github.com/pathikrit/better-files#streams

/**
  * Created by rajeevprasanna on 9/19/17.
  */
object FileUtils {

  //If directory is given wrong, then it throws an error : Failure(java.lang.NullPointerException)
  // success => Success(List[File])
  def getListOfFiles(dirPath:String, extensions:List[String], prefixes:List[String]):Try[List[File]] = {
    Try{
      val dir = file"$dirPath"
      dir.list.filter(!_.isDirectory).toList.filter { file =>
        extensions.contains(file.extension.getOrElse("")) && prefixes.exists(file.name.startsWith(_))
      }
    }
  }

  def extractFile(compressedFile:CompressedLogFile, outputDirectory:String): ExtractedFile = {
    val inFile = compressedFile.file
    val fin = Files.newInputStream(inFile.path)
    val in = new BufferedInputStream(fin)

    val outFile = file"${outputDirectory}/${inFile.name.split(inFile.extension.getOrElse("")).head}.log"
    val out = Files.newOutputStream(outFile.path)
    val gzIn = new GzipCompressorInputStream(in)

    val buffer = new Array[Byte](1024)
    var len = 0
    while (len != -1) {
      len = gzIn.read(buffer)
      if (len != -1) {
        out.write(buffer, 0, len)
      }
    }
    out.close()
    ExtractedFile(outFile, compressedFile)
  }

}
