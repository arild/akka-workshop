package examples

import akka.actor._

case class SayHello(name: String)

class GreetingActor extends Actor {

  def receive = {
    case SayHello => sender ! " a reply"
  }

}

object GreetingActor extends App {
  val system = ActorSystem("MySystem")
  val actorRef = system.actorOf(Props[GreetingActor])
  actorRef ! SayHello("Pope Benedict")
}
