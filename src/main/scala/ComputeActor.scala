import akka.actor.Actor
import akka.event.Logging

case class Addition(a: Integer, b: Integer)
case class Division(dividend: Int, divisor: Int)
case class GetNumCompletedTasks()

class ComputeActor extends Actor {
  val log = Logging(context.system, this)
  var numCompletedTasks: Integer = 0

  def receive = {
    case addition: Addition => {
      val result = addition.a + addition.b
      incrementNumCompletedTasks
      sender ! result
    }
    case division: Division => {
      val result: Integer = division.dividend / division.divisor
      incrementNumCompletedTasks
      sender ! result
    }
    case GetNumCompletedTasks =>  {
      sender ! numCompletedTasks
    }
    case m: Any => {
      log.info("Unhandled message");
      unhandled(m)
    }
  }

  def incrementNumCompletedTasks {
    numCompletedTasks += 1
  }
}