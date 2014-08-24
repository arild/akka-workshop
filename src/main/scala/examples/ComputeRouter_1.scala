package examples

import akka.actor._
import akka.event.Logging
import akka.routing._
import workshop.work.{RiskyAddition, RiskyAdditionResult, RiskyWork}

import scala.language.postfixOps

class ComputeRouter_1() extends Actor {
  val log = Logging(context.system, this)

  val router = context.actorOf(RoundRobinPool(50).props(Props[Routee]), "router1")

  def receive = {
    case work: RiskyWork => router.tell(work, self)
    case result: RiskyAdditionResult => println("Got result " + result.result + " from " + sender())
  }
}

class Routee extends Actor {
  def receive = {
    case work: RiskyWork => {
      println("Performing work")
      sender ! work.perform()
    }
  }
}

object ComputeRouterExample extends App {
  val system: ActorSystem = ActorSystem("MySystem")

  val router: ActorRef = system.actorOf(Props[ComputeRouter_1])
  router ! RiskyAddition(1, 1, 100)
  router ! RiskyAddition(1, 2, 100)
  router ! RiskyAddition(1, 3, 100)

  // There are better ways to ensure message are received before termination
  Thread.sleep(300)
  system.shutdown()
}