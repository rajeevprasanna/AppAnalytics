package com.rajeev.utils


import javax.ws.rs.core.MediaType

import better.files._
import com.rajeev.ConfigInitialzer
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import com.sun.jersey.api.client.{Client, ClientResponse}
import com.sun.jersey.core.util.MultivaluedMapImpl
import com.sun.jersey.multipart.FormDataMultiPart
import com.sun.jersey.multipart.file.FileDataBodyPart


/**
  * Created by rajeevprasanna on 9/20/17.
  */
object MailGunUtils extends ConfigInitialzer {

  //Refer : https://cloud.google.com/appengine/docs/flexible/java/sending-emails-with-mailgun

  val MAILGUN_API_KEY = getString("mailgun.api_key")
  val MAILGUN_DOMAIN_NAME = getString("mailgun.domain")

  val mailClient = Client.create()
  mailClient.addFilter(new HTTPBasicAuthFilter("api", MAILGUN_API_KEY))
  val webResource = mailClient.resource("https://api.mailgun.net/v3/" + MAILGUN_DOMAIN_NAME  + "/messages")

  def sendEmail = (errorLogFile:File) => {
//    val formData = new FormDataMultiPart()
//    formData.field("from", s"AppAnalytics<mailgun@$MAILGUN_DOMAIN_NAME>")
//    formData.field("to", getString("mailgun.toPpl"))
//    formData.field("subject", "Plugin app analytics")
//    formData.field("html", "<html><strong>Check Errors in attachments</strong></html>")
//    formData.bodyPart(new FileDataBodyPart("attachment", errorLogFile.toJava, MediaType.TEXT_PLAIN_TYPE))
//
//    val response = webResource.`type`(MediaType.MULTIPART_FORM_DATA_TYPE).post(classOf[ClientResponse], formData)
//    val textEntity = response.getEntity(classOf[String])
//    assert(response.getStatus() == 200, textEntity)
  }

  def sendTextMail = (mailText:String) => {
    val formData = new MultivaluedMapImpl()
    formData.add("from", s"AppAnalytics<mailgun@$MAILGUN_DOMAIN_NAME>")
    formData.add("to", getString("mailgun.toPpl"))
    formData.add("subject", "Application Error : Plugin app analytics")
    formData.add("html", s"<html><strong>${mailText}</strong></html>")

    val response = webResource.`type`(MediaType.APPLICATION_FORM_URLENCODED).post(classOf[ClientResponse], formData)
    val textEntity = response.getEntity(classOf[String])
    assert(response.getStatus() == 200, textEntity)
  }

}
