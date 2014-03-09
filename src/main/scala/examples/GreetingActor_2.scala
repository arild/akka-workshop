package examples

import akka.actor._

case class SayHello(name: String)

class GreetingActor_2 extends Actor {

  def receive = {
    case hello : SayHello => {
      println("Hello " + hello.name)
      sender ! " a reply"
    }
  }

}

object GreetingActor_2 extends App {
  val system = ActorSystem("MySystem")
  val actorRef = system.actorOf(Props(new GreetingActor_2))
  actorRef ! SayHello("Pope Benedict")
}
