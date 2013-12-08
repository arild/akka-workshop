import akka.actor.Actor
import akka.event.Logging

class ComputeSupervisor(computeActorFactory: ComputeActorFactory) extends Actor {

  val log = Logging(context.system, this)

  def receive = {
    case actorName: String => {
      computeActorFactory.create(context, actorName)
      log.info("created child with name {}", actorName)
    }
  }
}