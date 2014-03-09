package workshop

import akka.actor.{Terminated, ActorRef, Actor}
import akka.event.Logging
import workshop.work.{RiskyWorkResult, RiskyWork}


class ClientActor(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[RiskyWork]) extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    //TODO
    case _ => {}
  }
}
