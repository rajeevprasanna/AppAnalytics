package com.rajeev.utils

import java.io.File
import java.util.zip
import scala.util.Try
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedInputStream
import java.nio.file.Files
import java.nio.file.Paths


/**
  * Created by rajeevprasanna on 9/19/17.
  */
object FileUtils {

  //If directory is given wrong, then it throws an error : Failure(java.lang.NullPointerException)
  // success => Success(List[File])
  def getListOfFiles(dirPath:String, extensions:List[String], prefixes:List[String]):Try[List[File]] = {
    Try{
      val dir = new File(dirPath)
      dir.listFiles.filter(_.isFile).toList.filter { file =>
        extensions.exists(file.getName.endsWith(_)) && prefixes.exists(file.getName.startsWith(_))
      }
    }
  }

  def extractFile(fileName:String, dirPath:String): File = {
    val inFile = new File(s"$dirPath/$fileName")
    val fin = Files.newInputStream(inFile.toPath)
    val in = new BufferedInputStream(fin)

    val outFile = new File(s"${dirPath}/${fileName.split(".gz").head}.log")
    val out = Files.newOutputStream(outFile.toPath)
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
    outFile
  }



}
