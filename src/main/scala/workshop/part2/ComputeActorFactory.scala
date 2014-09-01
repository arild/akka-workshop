package workshop.part2

import scala.concurrent.duration._
import akka.actor.{Props, ActorRef, ActorContext}
import workshop.part1.ComputeActor


class ComputeActorFactory(numCompletedTaskActor: ActorRef, logCompletedTasksInterval: FiniteDuration = 1 second) {
  def create(context: ActorContext, actorName: String): ActorRef = {
    context.actorOf(Props(classOf[ComputeActor], numCompletedTaskActor, logCompletedTasksInterval), actorName)
  }
}