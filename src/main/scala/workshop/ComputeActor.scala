package workshop

import akka.actor.Actor
import akka.event.Logging
import scala.concurrent.duration._
import workshop.work.RiskyWork


case class Addition(a: Int, b: Int)
case class Division(dividend: Int, divisor: Int)
case class GetNumCompletedTasks()
case class NumCompletedTasks(numCompleted: Int)
case class Tick()

class ComputeActor(logCompletedTasksInterval: FiniteDuration) extends Actor {
  val log = Logging(context.system, this)
  var numCompletedTasks: Int = 0

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
      val result: Int = division.dividend / division.divisor
      incrementNumCompletedTasks()
      sender ! result
    }
    case work: RiskyWork => {
      val result = work.perform()
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
  }

  def incrementNumCompletedTasks() {
    numCompletedTasks += 1
  }

  def scheduleTick() {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(logCompletedTasksInterval, self, Tick)
  }
}