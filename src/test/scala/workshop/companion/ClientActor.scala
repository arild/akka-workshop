package workshop.companion

import akka.actor.{Props, ActorRef}
import workshop.work.RiskyWork
import workshop.ClientActor

// Companion object would usually be located in same file as class
object ClientActor {
  def props(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[RiskyWork]) = {
    Props(new ClientActor(computeSupervisor, resultActor, work))
  }
}