package examples

import akka.actor.{Actor, ActorSystem, Props, Terminated}

class DeathWatchActor extends Actor {

  override def preStart() {
    val volatileGreetingActor = context.actorOf(Props[VolatileGreetingActor])
    context.watch(volatileGreetingActor)
    volatileGreetingActor ! "print this message, please!"
  }

  def receive = {
    case Terminated(_) => println("looks like an actor has died :(")
  }
}

class VolatileGreetingActor extends Actor {

  def receive = {
    case s: String => {
      println("stopping")
      context.stop(self)
    }
  }
}

object DeathWatchActor extends App {
  val system = ActorSystem("MySystem")
  system.actorOf(Props[DeathWatchActor])

  system.shutdown()
}
