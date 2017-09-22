package com.rajeev.temp.calculator

import com.rajeev.temp.calculator.Models._
import akka.actor.ActorLogging
import akka.persistence.{PersistentActor, RecoveryCompleted}

/**
  * Created by rajeevprasanna on 9/15/17.
  */
class Calculator extends PersistentActor with ActorLogging {

  //Persistence Id must be unique for persisting actors as Akka Persistence uses this ID to recover state
  override def persistenceId: String = "calculator-persistence-actor"

  //internal state of the actor
  var state = CalculatorResult(result = 0)

  //General updateState method for reuse
  //Used by both receiveRecover and receiveCommand
  //This updates the internal state using an event
  val updateState:Event => Unit = {
    //don't need to use event => event.match {} boilerplate
    case Reset => state = state.reset
    case Added(value) => state = state.add(value)
    case Subtracted(value) => state = state.subtract(value)
    case Divided(value) => state = state.divide(value)
    case Multiplied(value) => state = state.multiply(value)
  }

  //Events come here (recovery phase) from database (snapshot and event)
  override def receiveRecover: Receive = {
    //comes from the event database journal
    case event:Event =>
      println(s"event receive in receiveRecover => $event")
      updateState(event)

    //this message is sent once recovery has completed
    case RecoveryCompleted => log.info(s"Recovery has completed for $persistenceId")
  }

  //commands come here (active phase)
  override def receiveCommand: Receive = {
    //Add this number to the result
    case Add(value) =>
        //convert command to event
        //We generate an Added Event (as in 'I have added this')
        val event = Added(value)
        persist(event)(updateState)

    case Subtract(value) => persist(Subtracted(value))(updateState)

    case Divide(value) =>
        if(value > 0) persist(Divided(value))(updateState)
        else log.error("Cannot divide by 0, Ignoring command")

    case Multiply(value) => persist(Multiplied(value))(updateState)

    case PrintResult => println(s"The result is : ${state.result}")

    case GetResult => sender ! state.result

    case Clear => persist(Reset)(updateState)
  }

}
