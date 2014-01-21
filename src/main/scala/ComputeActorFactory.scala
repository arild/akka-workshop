import akka.actor.{ActorRef, Props, ActorContext}

class ComputeActorFactory {
  def create(context: ActorContext, actorName: String): ActorRef = {
    context.actorOf(Props(classOf[ComputeActor]), actorName)
  }
}
