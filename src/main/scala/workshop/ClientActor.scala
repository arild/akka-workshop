package workshop

import akka.actor.{Props, Terminated, ActorRef, Actor}
import akka.event.Logging
import workshop.work.HeavyWork

object ClientActor {
  def props(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[HeavyWork]) = {
    Props(new ClientActor(computeSupervisor, resultActor, work))
  }
}

class ClientActor(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[HeavyWork]) extends Actor {
  val log = Logging(context.system, this)

  override def preStart() = {
    computeSupervisor ! StartComputeActor("computeActor")
  }

  def receive = {
    case computeActor: ActorRef => {
      context.watch(computeActor)
    }
    case Terminated => {
      log.error("Compute actor terminated, terminating self")
      context.stop(self)
    }
  }
}
