package examples

import akka.actor._

class GreetingActor_1 extends Actor {

  def receive = {
    case message: String => println("Hello " + message)
  }
}

object GreetingActor_1 extends App {
  val system = ActorSystem("MySystem")

  val greetingActor: ActorRef = system.actorOf(Props[GreetingActor_1])
  greetingActor ! "Hulk Hogan"

  // There are better ways to ensure message are received before termination
  Thread.sleep(100)
  system.shutdown()
}
