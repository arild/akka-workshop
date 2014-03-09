package examples

import akka.actor._

class GreetingActor_1 extends Actor {

  def receive = {
    case message : String => println("Hello " + message)
  }

}

object GreetingActor_1 extends App {
  val system = ActorSystem("MySystem")
  val actorRef = system.actorOf(Props(new GreetingActor_1))
  actorRef ! "Hulk Hogan"
}
