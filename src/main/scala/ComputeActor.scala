package workshop

import akka.actor.{Props, Actor}
import akka.event.Logging
import scala.concurrent.duration._


case class Addition(a: Integer, b: Integer)
case class Division(dividend: Int, divisor: Int)
case class GetNumCompletedTasks()
case class NumCompletedTasks(numCompleted: Integer)
case class Tick()

object ComputeActor {
  def props(logCompletedTasksInterval: FiniteDuration): Props = Props(new ComputeActor(logCompletedTasksInterval))
}

class ComputeActor(logCompletedTasksInterval: FiniteDuration) extends Actor {
  val log = Logging(context.system, this)
  var numCompletedTasks: Integer = 0

  override def preStart() = {
    scheduleTick()
  }

  def receive = {
    case addition: Addition => {
      val result = addition.a + addition.b
      incrementNumCompletedTasks()
      sender ! result
    }
    case division: Division => {
      val result: Integer = division.dividend / division.divisor
      incrementNumCompletedTasks()
      sender ! result
    }
    case GetNumCompletedTasks =>  {
      sender ! NumCompletedTasks(numCompletedTasks)
    }
    case Tick => {
      log.info("Num completed tasks: {}", numCompletedTasks)
      scheduleTick()
    }
    case m: Any => {
      log.info("Unhandled message");
      unhandled(m)
    }
  }

  def incrementNumCompletedTasks() {
    numCompletedTasks += 1
  }

  import context.dispatcher
  def scheduleTick() {
    context.system.scheduler.scheduleOnce(logCompletedTasksInterval, self, Tick)
  }
}