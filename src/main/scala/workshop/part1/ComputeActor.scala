package workshop.part1

import akka.actor.Actor
import akka.event.Logging
import scala.concurrent.duration._
import workshop.work.RiskyWork


case class Division(dividend: Int, divisor: Int)
object GetNumCompletedTasks
case class NumCompletedTasks(numCompleted: Int)
object LogNumCompletedTasks

class ComputeActor(logCompletedTasksInterval: FiniteDuration) extends Actor {
  val log = Logging(context.system, this)
  var numCompletedTasks: Int = 0

  override def preStart() = {
    scheduleLogNumCompletedTasks()
  }

  def receive = {
    case s: String => {
      incrementNumCompletedTasks()
      sender ! s.length
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
    case LogNumCompletedTasks => {
      log.info("Num completed tasks: {}", numCompletedTasks)
      scheduleLogNumCompletedTasks()
    }
  }

  def incrementNumCompletedTasks() {
    numCompletedTasks += 1
  }

  def scheduleLogNumCompletedTasks() {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(logCompletedTasksInterval, self, LogNumCompletedTasks)
  }
}
