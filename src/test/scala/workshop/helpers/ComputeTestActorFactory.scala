package workshop.helpers;

import akka.actor.{Props, ActorContext, ActorRef}
import workshop.part2.ComputeActorFactory


class ComputeTestActorFactory extends ComputeActorFactory {
  override def create(context:ActorContext, actorName: String): ActorRef= {
    context.actorOf(Props(classOf[ComputeTestActor]), actorName)
  }
}