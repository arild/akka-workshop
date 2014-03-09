package examples.example3

import akka.actor._

case class Tick()

class GreetingActor extends Actor {

  override def preStart() = {
    scheduleTick()
  }

  def scheduleTick() {
    import context.dispatcher
    context.system.scheduler.scheduleOnce(logCompletedTasksInterval, self, Tick)
  }

  def receive = {
    case SayHello => sender ! " a reply"
  }

}

object GreetingActor extends App {
  val system = ActorSystem("MySystem")
  val actorRef = system.actorOf(Props[GreetingActor])
  actorRef ! SayHello("Pope Benedict")
}
