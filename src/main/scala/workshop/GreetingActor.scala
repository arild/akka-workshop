package workshop

import akka.actor.{Props, ActorSystem, Actor}

class GreetingActor extends Actor {
  def receive = {
    case message : String ⇒ println("Hello " + message)
  }

  val system = ActorSystem("MySystem")
  val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
  greeter ! "Charlie Parker"
}
