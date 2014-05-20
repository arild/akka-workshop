package workshop.companion

import akka.actor.{Props, ActorRef}

// Companion object would usually be located in same file as class
object ClientActor {
  def props(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[workshop.work.RiskyWork]) = {
    Props(new workshop.ClientActor(computeSupervisor, resultActor, work))
  }
}