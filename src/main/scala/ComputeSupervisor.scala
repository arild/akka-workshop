import akka.actor.{ActorRef, Actor}
import akka.event.Logging

case class StartComputeActor(actorName: String)

class ComputeSupervisor(computeActorFactory: ComputeActorFactory) extends Actor {

  val log = Logging(context.system, this)

  def this() = this(new ComputeActorFactory())

  def receive = {
    case startComputeActor : StartComputeActor => {
      val computeActor: ActorRef = computeActorFactory.create(context, startComputeActor.actorName)
      sender ! computeActor
      log.info("created compute-actor with name {}", startComputeActor.actorName)
    }
  }
}
