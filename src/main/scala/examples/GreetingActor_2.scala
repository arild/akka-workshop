package examples

import akka.actor._

case class SayHello(name: String)

class GreetingActor_2 extends Actor {

  def receive = {
    case hello: SayHello => {
      println("Hello " + hello.name)
      sender ! hello.name
    }
  }
}

object GreetingActor_2 extends App {
  val system = ActorSystem("MySystem")

  val greetingActor = system.actorOf(Props[GreetingActor_2])
  greetingActor ! SayHello("Pope Benedict")

  // There are better ways to ensure message are received before termination
  Thread.sleep(100)
  system.shutdown()
}
