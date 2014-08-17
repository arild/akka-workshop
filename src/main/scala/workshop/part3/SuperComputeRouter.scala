package workshop.part3

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.event.Logging
import akka.routing._
import workshop.work.RiskyWork

import scala.concurrent.duration._
import scala.language.postfixOps

class SuperComputeRouter extends Actor {
  val log = Logging(context.system, this)

  val resizer = DefaultResizer(lowerBound = 5, upperBound = 20, messagesPerResize = 1)
  val router: ActorRef =
    context.actorOf(RoundRobinPool(10, Some(resizer)).props(Props[Worker]), "router1")

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute, loggingEnabled = false) {
      case _: Exception => Resume
    }

  def receive = {
    case w : RiskyWork =>
      router.tell(w, sender())
  }
}




