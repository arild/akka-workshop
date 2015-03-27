package workshop.part1

import akka.actor.{Actor, ActorRef}
import workshop.work.RiskyWork

import scala.concurrent.duration._

case class Division(dividend: Int, divisor: Int)
object GetNumCompletedTasks
case class NumCompletedTasks(numCompleted: Int)


class ComputeActor(numCompletedTaskActor: ActorRef, logCompletedTasksInterval: FiniteDuration) extends Actor {

  def receive = {
    //TODO
    case _ => {}
  }
}
