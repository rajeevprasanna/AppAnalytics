package com.rajeev

import akka.actor.{Actor, ActorLogging}

/**
  * Created by rajeevprasanna on 9/20/17.
  */
class MailSender extends Actor with ActorLogging {

  override def receive: Receive = {
    case _ => log.info("got request to send email using mail gun")
  }

}
