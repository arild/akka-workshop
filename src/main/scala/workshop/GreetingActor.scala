package workshop

import akka.actor.{Props, ActorSystem, Actor}

case class MyCommand()

class GreetingActor extends Actor {
  def receive = {
    case message : String => println("Hello " + message)
    case command : MyCommand => println("Got a command!")
  }

  val system = ActorSystem("MySystem")
  val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
  greeter ! "Charlie Parker"
  greeter ! MyCommand
}
