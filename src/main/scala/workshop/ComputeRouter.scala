package workshop

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{OneForOneStrategy, Props, Actor, ActorRef}
import akka.event.Logging
import workshop.work.RiskyWork
import akka.routing._
import akka.actor.SupervisorStrategy.Resume

class ComputeRouter() extends Actor {
  val log = Logging(context.system, this)

  val resizer = DefaultResizer(lowerBound = 5, upperBound = 10, messagesPerResize = 1)
  val router: ActorRef =
    context.actorOf(RoundRobinPool(50, Some(resizer)).props(Props[Routee]), "router1")

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: Exception                => Resume
    }

  def receive = {
    case w : RiskyWork =>
      router.tell(w, sender())
  }
}

class Routee extends Actor {
  def receive = {
    case riskyWork: RiskyWork => {
      sender ! riskyWork.perform()
    }
  }
}



