package workshop

import akka.actor.{Terminated, ActorRef, Actor}
import akka.event.Logging
import workshop.work.{HeavyWorkResult, HeavyWork}


class ClientActor(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[HeavyWork]) extends Actor {
  val log = Logging(context.system, this)

  override def preStart() = {
    computeSupervisor ! StartComputeActor("computeActor")
  }

  def receive = {
    case computeActor: ActorRef => {
      context.watch(computeActor)
      work.foreach(w => computeActor ! w)
    }
    case result: HeavyWorkResult => {
      resultActor ! result
    }
    case Terminated => {
      log.error("Compute actor terminated, terminating self")
      context.stop(self)
    }
  }
}
