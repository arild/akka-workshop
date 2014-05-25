package workshop

import scala.language.postfixOps
import akka.actor.{Props, Actor}
import akka.event.Logging
import workshop.work.RiskyWork
import akka.routing.{RoundRobinRoutingLogic, Router, ActorRefRoutee}

class ComputeRouter() extends Actor {
  val log = Logging(context.system, this)

  var router = {
    val routees = Vector.fill(5) {
      val r = context.actorOf(Props[Routee])
      context watch r
      ActorRefRoutee(r)
    }
    Router(RoundRobinRoutingLogic(), routees)
  }

  def receive = {
    case s: String => sender ! "i'm alive.."
    case w : RiskyWork =>
      router.route(w, sender())
  }
}

class Routee extends Actor {
  def receive = {
    case riskyWork: RiskyWork => {
      sender ! riskyWork.perform()
    }
  }
}



