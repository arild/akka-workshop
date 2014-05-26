package examples

import scala.language.postfixOps
import akka.actor._
import akka.event.Logging
import workshop.work.{RiskyAddition, RiskyWork}
import akka.routing._

class ComputeRouter_1() extends Actor {
  val log = Logging(context.system, this)

  val router: ActorRef =
    context.actorOf(RoundRobinPool(50).props(Props[Routee]), "router1")

  def receive = {
    case w : RiskyWork => router.tell(w, sender())
  }
}

class Routee extends Actor {
  def receive = {
    case riskyWork: RiskyWork => sender ! riskyWork.perform()
  }
}

object ComputeRouterExample extends App {
  val router: ActorRef = ActorSystem("MySystem").actorOf(Props[ComputeRouter_1])
  router ! RiskyAddition(1,1,100)
}