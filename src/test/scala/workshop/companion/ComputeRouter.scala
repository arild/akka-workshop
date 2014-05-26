package workshop.companion

import akka.actor.Props

// Companion object would usually be located in same file as class
object ComputeRouter {
  def props(computeActorFactory: workshop.ComputeActorFactory): Props = Props(new workshop.SuperComputeRouter())
}
