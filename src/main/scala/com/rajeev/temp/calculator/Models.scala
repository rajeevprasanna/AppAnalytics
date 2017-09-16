package com.rajeev.temp.calculator

/**
  * Created by rajeevprasanna on 9/15/17.
  */
object Models {

  sealed trait Command
  case object Clear extends Command
  case class Add(value:Double) extends Command
  case class Subtract(value:Double) extends Command
  case class Divide(value:Double) extends Command
  case class Multiply(value:Double) extends Command
  case object PrintResult extends Command
  case object GetResult extends Command

  //Events:
  sealed trait Event
  case object Reset extends Event
  case class Added(value:Double) extends Event
  case class Subtracted(value:Double) extends Event
  case class Divided(value:Double) extends Event
  case class Multiplied(value:Double) extends Event

  //Internal state for the calculator actor
  case class CalculatorResult(result:Double = 0){
    def reset = copy(result = 0)
    def add(value:Double) = copy(result = result + value)
    def subtract(value:Double) = copy(result = result - value)
    def multiply(value:Double) = copy(result = result * value)
    def divide(value:Double) = copy(result = result / value)
  }

}
