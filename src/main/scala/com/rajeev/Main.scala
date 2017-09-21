package com.rajeev

import akka.actor.Props
import org.slf4j.LoggerFactory

/**
  * Created by rajeevprasanna on 9/13/17.
  */
object Main extends App with ConfigInitialzer {

  final val log = LoggerFactory.getLogger(Main.getClass)
  log.info(s"App analytics started running...")

  val supervisorActor = system.actorOf(Props[SupervisorActor], "supervisor-actor")
  supervisorActor ! "TRIGGER_APP_ANALYTICS"
}
























//  system.terminate()
//  val supervisingActor = system.actorOf(Props[SupervisingActor], "supervising-actor")
//  supervisingActor ! "failChild"
//


//  val firstRef = system.actorOf(Props[PrintMyActorRefActor], "first-actor")
//  println(s"First : $firstRef")
//  firstRef ! "printit"
//
//  println(">>> Press enter to exit <<<")
//  try StdIn.readLine()
//  finally system.terminate()


//  To stop an actor, the recommended pattern is to call context.stop(self) inside the actor to stop itself,
  // usually as a response to some user defined stop message or when the actor is done with its job.
  // Stopping another actor is technically possible by calling context.stop(actorRef), but It is considered a bad practice to stop arbitrary actors this way:
  // try sending them a PoisonPill or custom stop message instead.

//  //Loading config
//  val appName = config.getString("appName")
//  println("appName ===> " + appName)
//
//  val Tick = "tick"
//  class TickActor extends Actor {
//    override def receive: Receive = {
//      case Tick => println("Message tick received in actor")
//    }
//  }
//
//  //scheduling concept
//  val tickActor = system.actorOf(Props[TickActor], "tickActor")
//  val cancellable = system.scheduler.schedule(0 milliseconds, 1000 milliseconds, tickActor, Tick)
//  cancellable.cancel()
//
//  //break the app if it is not cancelled
//  assert(cancellable.isCancelled)











//class PrintMyActorRefActor extends Actor {
//  override def receive: Receive = {
//    case "printit" =>
//            val secondRef = context.actorOf(Props.empty, "second-actor")
//            println(s"Second : $secondRef")
//  }
//}
//
//class SupervisingActor extends Actor {
//  val child = context.actorOf(Props[SupervisedActor], "supervised-actor")
//
//  override def receive: Receive = {
//    case "failChild" => child ! "fail"
//  }
//}
//
//class SupervisedActor extends Actor {
//  override def preStart(): Unit =  println("supervised actor started")
//  override def aroundPostStop(): Unit = println("supervised actor stopped")
//
//  override def receive: Receive = {
//    case "fail" =>
//      println("supervised actor fails now")
//      throw new Exception("I failed!")
//  }
//}
