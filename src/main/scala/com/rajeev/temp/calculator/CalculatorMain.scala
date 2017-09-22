package com.rajeev.temp.calculator

import com.rajeev.temp.calculator.Models._
import akka.actor.{ActorSystem, Props}

/**
  * Created by rajeevprasanna on 9/15/17.
  */
object CalculatorMain extends App {

  val system = ActorSystem("calculator-actor-system")
  val persistentCalculatorActor = system.actorOf(Props[Calculator])

  //send messages
  persistentCalculatorActor ! PrintResult
  persistentCalculatorActor ! Add(2)
  persistentCalculatorActor ! Multiply(2)
  persistentCalculatorActor ! PrintResult

  //Wait for 1 second before terminating
  Thread sleep 1000
  system terminate
}
