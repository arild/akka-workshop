package workshop.companion

import akka.actor.{Props, ActorRef}
import workshop.work.HeavyWork
import workshop.ClientActor

// Companion object would usually be located in same file as class
object ClientActor {
  def props(computeSupervisor: ActorRef, resultActor: ActorRef, work: List[HeavyWork]) = {
    Props(new ClientActor(computeSupervisor, resultActor, work))
  }
}