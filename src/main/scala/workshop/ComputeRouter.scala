package workshop

import scala.language.postfixOps
import akka.actor.{Props, Actor, ActorRef}
import akka.event.Logging
import workshop.work.RiskyWork
import akka.routing._

class ComputeRouter() extends Actor {
  val log = Logging(context.system, this)

  val resizer = DefaultResizer(lowerBound = 5, upperBound = 10, messagesPerResize = 1)
  val router: ActorRef =
    context.actorOf(RoundRobinPool(50, Some(resizer)).props(Props[Routee]), "router1")

  def receive = {
    case s: String => sender ! "i'm alive.."
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



