package examples.example1

import akka.actor._

case class CreateGreetingActor()

class GreetingActor extends Actor {
  def receive = {
    case message : String => println("Hello " + message)
  }

}

object GreetingActor extends App {
  val system = ActorSystem("MySystem")
  val actorRef = system.actorOf(Props[GreetingActor])
  actorRef ! "Hulk Hogan"
}
