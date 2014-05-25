package examples

import scala.language.postfixOps
import akka.actor._
import akka.event.Logging
import workshop.work.{RiskyAddition, RiskyWork}
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}
import akka.routing.Router
import workshop.work.RiskyAddition
import akka.routing.ActorRefRoutee

class ComputeRouter_1() extends Actor {
  val log = Logging(context.system, this)

  // Create 5 routers and set RoundRobinRoutingLogic on them
  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[Routee])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case w : RiskyWork =>
      println("Routing this to one of my routee's ..")
      router.route(w, sender())
    case Terminated(a) =>
      println("One of my Routees have died..")
      router = router.removeRoutee(a)
      val r = context.actorOf(Props[Routee])
      context watch r
      router = router.addRoutee(r)
  }
}

class Routee extends Actor {
  def receive = {
    case riskyWork: RiskyWork => {
      println("Routee performing RiskyWork")
      sender ! riskyWork.perform()
    }
  }
}

object ComputeRouterExample extends App {
  val system = ActorSystem("MySystem")
  val router: ActorRef = system.actorOf(Props[ComputeRouter_1])
  router ! RiskyAddition(1,1,100)
}



