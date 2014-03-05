package workshop.companion

import workshop.{ComputeSupervisor, ComputeActorFactory}
import akka.actor.Props

// Companion object would usually be located in same file as class
object ComputeSupervisor {
  def props(computeActorFactory: ComputeActorFactory): Props = Props(new ComputeSupervisor(computeActorFactory))
}