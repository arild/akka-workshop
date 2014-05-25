package workshop

import akka.actor.Actor
import akka.event.Logging
import scala.concurrent.duration._
import workshop.work.RiskyWork


case class Division(dividend: Int, divisor: Int)
object GetNumCompletedTasks
case class NumCompletedTasks(numCompleted: Int)

class ComputeActor(logCompletedTasksInterval: FiniteDuration) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    //TODO
    case _ => {}
  }
}
