package workshop.companion

import scala.concurrent.duration.FiniteDuration
import akka.actor.Props
import workshop.ComputeActor

// Companion object would usually be located in same file as class
object ComputeActor {
  def props(logCompletedTasksInterval: FiniteDuration): Props = Props(new ComputeActor(logCompletedTasksInterval))
}