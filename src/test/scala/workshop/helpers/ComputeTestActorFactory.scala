package workshop.helpers;

import akka.actor.{Props, ActorContext, ActorRef}
import workshop.ComputeActorFactory;


class ComputeActorTestFactory extends ComputeActorFactory {
  override def create(context:ActorContext, actorName: String): ActorRef= {
    context.actorOf(Props(classOf[ComputeTestActor]), actorName)
  }
}