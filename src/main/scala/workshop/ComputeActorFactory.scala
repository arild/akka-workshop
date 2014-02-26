package workshop

import scala.concurrent.duration._
import akka.actor.{ActorRef, ActorContext}


class ComputeActorFactory(logCompletedTasksInterval: FiniteDuration = 1 second) {
  def create(context: ActorContext, actorName: String): ActorRef = {
    context.actorOf(ComputeActor.props(logCompletedTasksInterval), actorName)
  }
}
