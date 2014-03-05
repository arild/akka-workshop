package workshop

import scala.concurrent.duration._
import akka.actor.{Props, ActorRef, ActorContext}


class ComputeActorFactory(logCompletedTasksInterval: FiniteDuration = 1 second) {
  def create(context: ActorContext, actorName: String): ActorRef = {
    context.actorOf(Props(new ComputeActor(logCompletedTasksInterval)), actorName)
  }
}
